package com.example.core.dto;

import com.example.core.model.Roles;
import lombok.*;

import javax.validation.constraints.*;

/**
 * DTO (Data Transfer Object) для передачи данных пользователя.
 * Содержит поля для email, пароля и роли пользователя,
 * а также валидацию для каждого поля.
 */
@Getter
@Setter
public class UserDto {

    /**
     * Адрес электронной почты пользователя.
     * Должен быть корректного формата и не может быть пустым.
     */
    @Email(message = "Некорректный формат email!")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;

    /**
     * Пароль пользователя.
     * Не может быть пустым.
     */
    @NotBlank(message = "Password  не может быть пустым!")
    private String password;

    /**
     * Роль пользователя.
     * Не может быть пустой.
     */
    @NotBlank(message = "Role не может быть пуста!")
    private Roles role;
}
