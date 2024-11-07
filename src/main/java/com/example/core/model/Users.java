package com.example.core.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    private String role;
}