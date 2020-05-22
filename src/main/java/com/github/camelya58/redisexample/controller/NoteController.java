package com.github.camelya58.redisexample.controller;

import com.github.camelya58.redisexample.model.Note;
import com.github.camelya58.redisexample.repository.XmlOutRepository;
import com.github.camelya58.redisexample.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Обычный REST-контроллер для обработки входящих JSON-запросов
 *
 * @author Kamila Meshcheryakova
 */
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    private final XmlOutRepository xmlOutRepository;

    @PostMapping
    public Note create(@RequestBody Note note){
        String date = LocalDate.now().toString();
        Note neededNote = noteService.saveOrUpdate(note);
        xmlOutRepository.createAndSave(neededNote, date);
        return neededNote;
    }
    @GetMapping
    public Iterable<Note> getAll(){
        return noteService.findAll();
    }
    @GetMapping("/{id}")
    public Note getById(@PathVariable Long id){
        return noteService.getById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        noteService.deleteById(id);
    }
}
