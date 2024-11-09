package com.example.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Класс, представляющий событие.
 * Содержит информацию о событии, включая адрес электронной почты и описание.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    /**
     * Адрес электронной почты, связанный с событием.
     */
    private String email;

    /**
     * Описание события.
     */
    private String description;
}
