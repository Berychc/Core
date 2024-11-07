package com.example.core.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "images")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long fileSize;
    private String mediaType;

    @Lob
    private byte[] data;
}
