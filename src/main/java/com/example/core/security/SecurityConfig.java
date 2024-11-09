package com.example.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация безопасности приложения.
 * Определяет настройки для аутентификации и авторизации пользователей.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Конфигурация настроек HTTP безопасности.
     *
     * @param http объект HttpSecurity для настройки параметров безопасности.
     * @throws Exception если возникает ошибка в процессе настройки.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Отключение защиты от CSRF
                .authorizeRequests()
                .antMatchers("/user/register").permitAll() // Разрешить доступ к регистрации без аутентификации
                .antMatchers("/moderator/**").hasRole("MODERATOR") // Доступ только для модераторов
                .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                .and()
                .formLogin()
                .permitAll() // Разрешить доступ к форме входа для всех
                .and()
                .exceptionHandling(); // Обработка исключений
    }

    /**
     * Бин, предоставляющий реализацию UserDetailsService для аутентификации пользователей.
     *
     * @return реализация UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UsersDetailsService();
    }

    /**
     * Бин, предоставляющий аутентификационный провайдер.
     *
     * @return аутентификационный провайдер, который используется для аутентификации пользователей.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(encoder());
        return provider;
    }

    /**
     * Бин, предоставляющий кодировщик паролей.
     *
     * @return кодировщик паролей, используемый для шифрования паролей.
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
