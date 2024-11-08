package com.example.core.service;

import com.example.core.dto.UserDto;
import com.example.core.model.Event;
import com.example.core.model.Roles;
import com.example.core.model.Users;
import com.example.core.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
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

    @Autowired
    private ObjectMapper objectMapper;


    public void registerUser(UserDto userDto) {
        if (userDto == null || userDto.getEmail() == null) {
            throw new RuntimeException("Email не может быть пустым!");
        }
        // Проверка на существование пользователя с таким email
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует!");
        }
        Users user = new Users();
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        repository.save(user);
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

    public void blockedUser(Integer id) {
        Users user = repository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setBlocked(true);
        repository.save(user);
    }

    public void unblockedUser(Integer id) {
        Users user = repository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setBlocked(false);
        repository.save(user);
    }
}
