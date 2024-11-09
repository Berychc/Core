package com.example.core.controller;

import com.example.core.model.Image;
import com.example.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Контроллер для управления пользователями модераторами.
 * Предоставляет методы для блокировки и разблокировки пользователей,
 * а также для получения отфильтрованных изображений.
 */
@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    @Autowired
    private UserService service;

    /**
     * Блокировка пользователя по его ID.
     * Доступно только пользователям с ролью 'MODERATOR'.
     *
     * @param id ID пользователя, которого нужно заблокировать.
     * @return ResponseEntity с сообщением о результате операции блокировки.
     */
    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> blockUser(@PathVariable Integer id) {
        try {
            service.blockedUser(id);
            return ResponseEntity.ok(String.format("User с %s - заблокирован", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при блокировке пользователя: " + e.getMessage());
        }
    }

    /**
     * Разблокировка пользователя по его ID.
     * Доступно только пользователям с ролью 'MODERATOR'.
     *
     * @param id ID пользователя, которого нужно разблокировать.
     * @return ResponseEntity с сообщением о результате операции разблокировки.
     */
    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> unblockUser(@PathVariable Integer id) {
        try {
            service.unblockedUser(id);
            return ResponseEntity.ok(String.format("User с %s - разблокирован", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при разблокировке пользователя: " + e.getMessage());
        }
    }

    /**
     * Получение отфильтрованных изображений для модерации.
     * Доступно только пользователям с ролью 'MODERATOR'.
     *
     * @param ids       Список ID изображений для фильтрации (необязательно).
     * @param minSize   Минимальный размер изображений для фильтрации (необязательно).
     * @param maxSize   Максимальный размер изображений для фильтрации (необязательно).
     * @param startDate Начальная дата для фильтрации изображений (необязательно).
     * @param endDate   Конечная дата для фильтрации изображений (необязательно).
     * @param sortBy    Поле, по которому требуется сортировать результаты (по умолчанию "uploadDate").
     * @param sortOrder Порядок сортировки результатов (по умолчанию "ASC").
     * @return ResponseEntity, содержащий список отфильтрованных изображений.
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<Image>> getFilteredImages(
            @RequestParam(required = false) List<Integer> ids,
            @RequestParam(required = false) Long minSize,
            @RequestParam(required = false) Long maxSize,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false, defaultValue = "uploadDate") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortOrder) {

        List<Image> images = service.getModeratedImages(ids, minSize, maxSize, startDate, endDate, sortBy, sortOrder);
        return ResponseEntity.ok(images);
    }
}
