package com.example.core.controller;

import com.example.core.dto.UserDto;
import com.example.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления пользователями.
 * Предоставляет методы для регистрации пользователей и тестового сообщения.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    /**
     * Тестовое сообщение, чтобы проверить, что контроллер работает.
     *
     * @return Строка с тестовым сообщением.
     */
    @GetMapping
    public String startMessage() {
        return "Test project";
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param userDto Объект, содержащий данные пользователя для регистрации.
     * @return ResponseEntity с сообщением о результате регистрации.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            System.out.println("Received UserDto: " + userDto);  // Логируем полученные данные
            service.registerUser(userDto);
            return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }
}
