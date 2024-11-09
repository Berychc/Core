package com.example.core.repository;

import com.example.core.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Интерфейс репозитория для работы с сущностями пользователей.
 * Предоставляет методы для взаимодействия с таблицей пользователей в базе данных.
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    /**
     * Поиск пользователя по адресу электронной почты.
     *
     * @param email Адрес электронной почты пользователя.
     * @return Optional содержащий пользователя, если он найден, или пустой объект, если нет.
     */
    Optional<Users> findByEmail(String email);

    /**
     * Проверка, существует ли пользователь с указанным адресом электронной почты.
     *
     * @param email Адрес электронной почты пользователя.
     * @return true, если пользователь существует, иначе false.
     */
    boolean existsByEmail(String email);
}
