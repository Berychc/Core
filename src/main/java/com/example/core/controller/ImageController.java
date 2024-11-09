package com.example.core.controller;

import com.example.core.model.Image;
import com.example.core.repository.ImageRepository;
import com.example.core.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Контроллер для работы с изображениями.
 * Предоставляет методы для загрузки, фильтрации и скачивания изображений.
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService service;

    @Autowired
    private ImageRepository repository;

    /**
     * Загрузка изображения, связанного с адресом электронной почты пользователя.
     *
     * @param file  Файл изображения, который нужно загрузить.
     * @param email Адрес электронной почты пользователя, загружающего изображение.
     * @return ResponseEntity, содержащий сообщение об успешной загрузке.
     * @throws Exception Если возникает ошибка во время загрузки.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload images")
    public ResponseEntity<String> uploadImages(
            @RequestParam MultipartFile file,
            @RequestParam String email) throws Exception {
        service.uploadImages(file, email);
        return ResponseEntity.ok("Изображение добавлено");
    }

    /**
     * Получение списка изображений для конкретного пользователя
     * с возможностью фильтрации по различным параметрам.
     *
     * @param userEmail Адрес электронной почты пользователя,
     *                  чьи изображения нужно получить.
     * @param ids       Список ID изображений для фильтрации результатов (необязательно).
     * @param minSize   Минимальный размер изображений для включения (необязательно).
     * @param maxSize   Максимальный размер изображений для включения (необязательно).
     * @param startDate Начальная дата для фильтрации изображений (необязательно).
     * @param endDate   Конечная дата для фильтрации изображений (необязательно).
     * @param sortBy    Поле, по которому нужно сортировать результаты
     *                  (по умолчанию "uploadDate").
     * @param sortOrder Порядок сортировки результатов
     *                  (по умолчанию "ASC").
     * @return ResponseEntity, содержащий список отфильтрованных изображений.
     */
    @GetMapping("/list")
    public ResponseEntity<List<Image>> getImages(
            @RequestParam String userEmail,
            @RequestParam(required = false) List<Integer> ids, // Можно передать список ID
            @RequestParam(required = false) Long minSize,      // Минимальный размер
            @RequestParam(required = false) Long maxSize,      // Максимальный размер
            @RequestParam(required = false) Date startDate,    // Начальная дата
            @RequestParam(required = false) Date endDate,      // Конечная дата
            @RequestParam(required = false, defaultValue = "uploadDate") String sortBy, // Поле для сортировки
            @RequestParam(required = false, defaultValue = "ASC") String sortOrder) { // Порядок сортировки

        List<Image> filteredImages = service.getFilteredImages(userEmail, ids, minSize, maxSize, startDate, endDate, sortBy, sortOrder);
        return ResponseEntity.ok(filteredImages); // Возвращаем список изображений в ответе
    }

    /**
     * Скачать изображение по его ID и электронной почте пользователя.
     *
     * @param imageId   ID изображения для скачивания.
     * @param userEmail Адрес электронной почты пользователя, запрашивающего скачивание.
     * @param response  HttpServletResponse для записи данных изображения.
     * @throws IOException Если возникает ошибка во время скачивания.
     */
    @GetMapping("/{imageId}/download")
    public void downloadImage(@PathVariable Integer imageId,
                              @RequestParam String userEmail,
                              HttpServletResponse response) throws IOException {
        service.downloadImage(imageId, userEmail, response);
    }
}
