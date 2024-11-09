package com.example.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;


/**
 * Модель пользователя, представляющая сущность 'Users' в базе данных.
 * Содержит информацию о пользователе, включая его идентификатор,
 * электронную почту, пароль, роль и состояние блокировки.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Электронная почта пользователя.
     */
    @Column(name = "email")
    private String email;

    /**
     * Пароль пользователя.
     */
    @Column(name = "password")
    private String password;

    /**
     * Роль пользователя (MODERATOR, USER).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @JsonIgnore
    private Roles role;

    /**
     * Флаг, указывающий, заблокирован ли пользователь.
     */
    @Column(name = "is_blocked")
    @JsonIgnore
    private boolean isBlocked;

    /**
     * Список изображений, связанных с пользователем.
     * Один пользователь может иметь несколько изображений.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;
}