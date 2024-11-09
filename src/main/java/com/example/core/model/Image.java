package com.example.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * Модель изображения, представляющая собой сущность в базе данных.
 * Содержит информацию об изображении, такой как имя, размер, тип содержимого,
 * дата загрузки и данные самого изображения.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {

    /**
     * Уникальный идентификатор изображения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Имя изображения.
     */
    @Column(name = "name")
    private String name;

    /**
     * Оригинальное имя файла, из которого было загружено изображение.
     */
    @Column(name = "original_file_name")
    private String originalFileName;

    /**
     * Размер файла изображения в байтах.
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Тип содержимого изображения.
     */
    @Column(name = "content_type")
    private String contentType;

    /**
     * Дата и время загрузки изображения.
     */
    @Column(name = "upload_date")
    @Temporal(TemporalType.TIMESTAMP) // временная метка
    private Date uploadDate;

    /**
     * Данные изображения в виде массива байтов.
     */
    @Lob
    @Column(name = "bytes")
    private byte[] bytes;

    /**
     * Пользователь, который загрузил изображение.
     * Это поле игнорируется при сериализации в JSON.
     */
    @JsonIgnore
    @ManyToOne // Указываем отношение многим к одному
    @JoinColumn(name = "user_id", nullable = false) // Внешний ключ для пользователя
    private Users user;
}



