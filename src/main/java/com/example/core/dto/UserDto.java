package com.example.core.dto;

import com.example.core.model.Roles;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserDto {

    @Email(message = "Некорректный формат email!")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;

    @NotBlank(message = "Password  не может быть пустым!")
    private String password;

    private Roles role;
}
