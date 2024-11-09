package com.example.core.service;

import com.example.core.model.Event;
import com.example.core.model.Image;
import com.example.core.model.Users;
import com.example.core.repository.ImageRepository;
import com.example.core.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


/**
 * Сервис для работы с изображениями.
 * Предоставляет методы для загрузки, скачивания и фильтрации изображений.
 */
@Service
@Slf4j
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;// Репозиторий для работы с изображениями

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate; // Интеграция с RabbitMQ

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * Загружает изображение, связывая его с пользователем по электронной почте.
     *
     * @param file      Файл изображения, который нужно загрузить.
     * @param userEmail Электронная почта пользователя, загружающего изображение.
     * @throws IOException Если возникает ошибка при работе с файлами.
     */
    @Transactional
    public void uploadImages(MultipartFile file, String userEmail) throws IOException {
        // Проверка допустимых форматов файлов
        String contentType = file.getContentType();
        if (!isValidImageFormat(contentType)) {
            throw new IllegalArgumentException("Неподдерживаемый формат файла: " + contentType);
        }

        // Получаем путь файла с расширением
        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());

        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        long fileSize = file.getSize();

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        // Получаем пользователя по email
        Users user = usersRepository.findByEmail(userEmail).orElseThrow(()
                -> new RuntimeException(String.format("Пользователь с %s не найден!")));


        // Сохранение информации о загружаемом изображении
        Image image = new Image();
        image.setOriginalFileName(file.getOriginalFilename());
        image.setName(file.getOriginalFilename());
        image.setFileSize(fileSize);
        image.setContentType(contentType);
        image.setBytes(file.getBytes());
        image.setUploadDate(new Date());
        image.setUser(user); // Устанавливаем связь с объектом Users

        imageRepository.save(image);

        // Отправка уведомления в mail-сервис
        sendMailNotification(userEmail, fileSize);
    }

    /**
     * Проверяет, является ли указанный формат изображения допустимым.
     *
     * @param contentType Тип контента изображения.
     * @return true, если формат допустим, иначе false.
     */

    private boolean isValidImageFormat(String contentType) {
        List<String> validFormats = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
        return validFormats.contains(contentType);
    }

    /**
     * Отправляет уведомление на почту пользователя о загрузке изображений.
     *
     * @param userEmail Адрес электронной почты пользователя.
     * @param totalSize Общий размер загруженных изображений в байтах.
     */
    private void sendMailNotification(String userEmail, Long totalSize) {
        try {
            Event event = new Event(userEmail, "Уведомление о загрузке изображений. Общий объём: " + totalSize + " байт.");

            // Сериализация объекта в JSON
            byte[] messageBody = objectMapper.writeValueAsBytes(event);

            // Установка заголовков
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");

            // Создание сообщения
            Message message = new Message(messageBody, messageProperties);

            // Отправка сообщения
            rabbitTemplate.send("mail", message);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Произошла ошибка %s", e.getMessage()));
        }
    }

    /**
     * Получает список изображений, отфильтрованных по указанным параметрам.
     *
     * @param userEmail Адрес электронной почты пользователя, которому принадлежат изображения.
     * @param ids       Список ID изображений для фильтрации (необязательно).
     * @param minSize   Минимальный размер изображений для фильтрации (необязательно).
     * @param maxSize   Максимальный размер изображений для фильтрации (необязательно).
     * @param startDate Начальная дата для фильтрации изображений (необязательно).
     * @param endDate   Конечная дата для фильтрации изображений (необязательно).
     * @param sortBy    Поле, по которому требуется сортировать результаты (по умолчанию "uploadDate").
     * @param sortOrder Порядок сортировки результатов ("ASC" или "DESC", по умолчанию "ASC").
     * @return Список отфильтрованных изображений.
     */
    @Transactional(readOnly = true)
    public List<Image> getFilteredImages(
            String userEmail, // Добавляем email пользователя
            List<Integer> ids,
            Long minSize,
            Long maxSize,
            Date startDate,
            Date endDate,
            String sortBy,
            String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        // Получаем текущего пользователя
        Users currentUser = usersRepository.findByEmail(userEmail).orElseThrow();
        if (currentUser == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        // Фильтруем изображения по пользователю
        if (ids != null && !ids.isEmpty()) {
            return imageRepository.findByIdInAndUser(ids, currentUser, sort);
        } else if (minSize != null && maxSize != null) {
            return imageRepository.findByFileSizeBetweenAndUser(minSize, maxSize, currentUser, sort);
        } else if (startDate != null && endDate != null) {
            return imageRepository.findByUploadDateBetweenAndUser(startDate, endDate, currentUser, sort);
        }

        // Если нет параметров фильтрации, возвращаем пустой список
        return Collections.emptyList();
    }

    /**
     * Скачивает изображение по его ID и отправляет уведомление на почту.
     *
     * @param imageId   ID изображения для скачивания.
     * @param userEmail Адрес электронной почты пользователя,
     *                  запрашивающего скачивание.
     * @param response  HttpServletResponse для записи данных изображения.
     * @throws IOException Если возникает ошибка во время скачивания.
     */
    public void downloadImage(Integer imageId, String userEmail, HttpServletResponse response) throws IOException {
        Image image = getImageById(imageId);

        // Проверяем, принадлежит ли изображение пользователю
        if (!Objects.equals(image.getUser().getEmail(), userEmail)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN); // Отправляем 403 Forbidden
            return;
        }

        // Настраиваем заголовки для скачивания
        response.setContentType(image.getContentType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getOriginalFileName() + "\"");
        response.setContentLength(image.getBytes().length);

        // Записываем байты изображения в выходной поток
        response.getOutputStream().write(image.getBytes());
        response.getOutputStream().flush();

        // Отправляем уведомление на почту
        sendMailNotification(userEmail, image.getFileSize(), image.getOriginalFileName());
    }

    /**
     * Отправляет уведомление пользователю по электронной почте о
     * скачивании изображения.
     *
     * @param userEmail        Адрес электронной почты пользователя.
     * @param fileSize         Размер файла изображения в байтах.
     * @param originalFileName Оригинальное имя файла изображения.
     * @throws RuntimeException Если возникает ошибка во время отправки уведомления.
     */
    private void sendMailNotification(String userEmail, Long fileSize, String originalFileName) {
        try {
            Event event = new Event(userEmail, "Изображение скачано: " + originalFileName + " (" + fileSize + " байт).");

            // Сериализация объекта в JSON
            byte[] messageBody = objectMapper.writeValueAsBytes(event);

            // Установка заголовков
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");

            // Создание сообщения
            Message message = new Message(messageBody, messageProperties);

            // Отправка сообщения в RabbitMQ
            rabbitTemplate.send("mail", message);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Произошла ошибка: %s", e.getMessage()));
        }
    }

    /**
     * Находит изображение по его ID.
     *
     * @param imageId ID изображения для поиска.
     * @return Объект изображения.
     * @throws RuntimeException Если изображение не найдено.
     */
    private Image getImageById(Integer imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));
    }
}
