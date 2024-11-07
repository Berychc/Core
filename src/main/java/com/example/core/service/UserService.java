package com.example.core.service;

import com.example.core.dto.UserDto;
import com.example.core.model.Event;
import com.example.core.model.Users;
import com.example.core.repository.UsersRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UsersRepository repository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PasswordEncoder encoder;


    public String registerUser(UserDto userDto) {
        if (userDto == null || userDto.getEmail() == null) {
            throw new RuntimeException("Email не может быть пустым!");
        }
        // Проверка на существование пользователя с таким email
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует!");
        }
        try {
            Users user = new Users();
            user.setEmail(userDto.getEmail());
            user.setPassword(encoder.encode(userDto.getPassword()));
            user.setRole("ROLE_USER");
            repository.save(user);

            Event event = new Event(userDto.getEmail(), "Приветственное сообщение");
            System.out.println("Sending event: " + event);
            rabbitTemplate.convertAndSend("mail", event); // Используйте этот rabbitTemplate
            return "OK";
        } catch (Exception e) {
            throw new RuntimeException(String.format("Произошла ошибка %s", e.getMessage()));
        }
    }
}
