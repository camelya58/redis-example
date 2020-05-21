package com.github.camelya58.redisexample.service;

import com.github.camelya58.redisexample.model.Note;
import com.github.camelya58.redisexample.repository.JedisRepository;
import com.github.camelya58.redisexample.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Класс NoteService предназначен для реализации методов MongoRepository
 *
 * @author Kamila Meshcheryakova
 */
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    private final JedisRepository jedisRepository;

    public Note saveOrUpdate(Note note){
        Note savedNote;
        if(note.getId() !=null && noteRepository.existsById(note.getId())){
            savedNote = noteRepository.findById(note.getId()).get();
            savedNote.setAuthor(note.getAuthor());
            savedNote.setId(note.getId());
            savedNote.setTitle(note.getTitle());
            savedNote.setContent(note.getContent());
        } else {
            savedNote = note;
            note.setId(jedisRepository.get());
        }
        return noteRepository.save(savedNote);
    }

    public Iterable<Note> findAll(){
        return noteRepository.findAll();
    }

    public Note getById(Long id){
        return noteRepository.findById(id).orElseThrow(() -> new RuntimeException("Note Not Found"));
    }

    public void deleteById(Long id){
        if(noteRepository.existsById(id)){
            noteRepository.deleteById(id);
        } else {
            throw new RuntimeException("Note not found by id");
        }

    }
}
