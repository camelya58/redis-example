package com.github.camelya58.redisexample.repository;

import com.github.camelya58.redisexample.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Класс NoteRepository предназначен для хранения note
 *
 * @author Kamila Meshcheryakova
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, Long> {
}
