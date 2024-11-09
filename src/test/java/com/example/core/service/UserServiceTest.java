package com.example.core.service;

import com.example.core.dto.UserDto;
import com.example.core.model.Image;
import com.example.core.model.Roles;
import com.example.core.model.Users;
import com.example.core.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;


    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    public void testRegisterUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userDto.setRole(Roles.USER);

        // Выполняем регистрацию
        userService.registerUser(userDto);

        // Ожидаем, что пользователь был сохранен
        Optional<Users> user = usersRepository.findByEmail("test@example.com");

        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals("test@example.com", user.get().getEmail());
        Assertions.assertEquals("encodedPassword", user.get().getPassword());
    }
}
