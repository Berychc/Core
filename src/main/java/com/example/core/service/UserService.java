package com.example.core.service;

import com.example.core.dto.UserDto;
import com.example.core.model.Event;
import com.example.core.model.Image;
import com.example.core.model.Users;
import com.example.core.repository.ImageRepository;
import com.example.core.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Сервис для управления пользователями и изображениями.
 * Предоставляет методы для регистрации пользователей, блокировки,
 * разблокировки и получения отфильтрованных изображений.
 */
@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Регистрация нового пользователя.
     *
     * @param userDto Объект, содержащий данные пользователя для регистрации.
     * @throws RuntimeException Если email пуст, пользователь с таким email уже существует
     * или произошла ошибка при отправке сообщения.
     */
    public void registerUser(UserDto userDto) {
        if (userDto == null || userDto.getEmail() == null) {
            throw new RuntimeException("Email не может быть пустым!");
        }
        // Проверка на существование пользователя с таким email
        if (usersRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует!");
        }
        Users user = new Users();
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        usersRepository.save(user);
        try {
            Event event = new Event(userDto.getEmail(), "Приветственное сообщение");

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
     * Получение отфильтрованных изображений для модерации.
     *
     * @param ids       Список ID изображений для фильтрации (необязательно).
     * @param minSize   Минимальный размер изображений для фильтрации (необязательно).
     * @param maxSize   Максимальный размер изображений для фильтрации (необязательно).
     * @param startDate Начальная дата для фильтрации изображений (необязательно).
     * @param endDate   Конечная дата для фильтрации изображений (необязательно).
     * @param sortBy    Поле, по которому требуется сортировать результаты (необязательно).
     * @param sortOrder Порядок сортировки результатов (необязательно).
     * @return Список отфильтрованных изображений.
     */
    @Transactional(readOnly = true)
    public List<Image> getModeratedImages(
            List<Integer> ids,
            Long minSize,
            Long maxSize,
            Date startDate,
            Date endDate,
            String sortBy,
            String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        // Фильтруем изображения
        if (ids != null && !ids.isEmpty()) {
            return imageRepository.findByIdIn(ids, sort);
        } else if (minSize != null && maxSize != null) {
            return imageRepository.findByFileSizeBetween(minSize, maxSize, sort);
        } else if (startDate != null && endDate != null) {
            return imageRepository.findByUploadDateBetween(startDate, endDate, sort);
        }

        // Если нет параметров фильтрации, возвращаем все изображения
        return imageRepository.findAll(sort);
    }

    /**
     * Блокировка пользователя по его ID.
     *
     * @param id ID пользователя, которого нужно заблокировать.
     * @throws RuntimeException Если пользователь с таким ID не найден.
     */
    public void blockedUser(Integer id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setBlocked(true);
        usersRepository.save(user);
    }

    /**
     * Разблокировка пользователя по его ID.
     *
     * @param id ID пользователя, которого нужно разблокировать.
     * @throws RuntimeException Если пользователь с таким ID не найден.
     */
    public void unblockedUser(Integer id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setBlocked(false);
        usersRepository.save(user);
    }
}
