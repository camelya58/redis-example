package com.github.camelya58.redisexample.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Класс Note является POJO-объектом и описывает входные данные
 *
 * @author Kamila Meshcheryakova
 */
@Data
@Document(value = "notes")
public class Note {

        @Id
        private Long id;

        private String author;
        private String title;
        private String content;

}
