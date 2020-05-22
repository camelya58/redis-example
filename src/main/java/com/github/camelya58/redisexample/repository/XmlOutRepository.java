package com.github.camelya58.redisexample.repository;

import com.github.camelya58.redisexample.model.Note;
import com.github.camelya58.redisexample.transfer.XmlHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс XmlOutRepository предназначен для записи xml файла в папке по указанному пути
 *
 *  @author Kamila Meshcheryakova
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class XmlOutRepository {

    private final XmlHelper xmlHelper;

    /**
     * Формирование xml файла для note
     *
     * @param note экземпляр входящего запроса
     * @param currentDate дата обработки запроса
     */
    public synchronized void createAndSave(Note note, String currentDate) {

        String xml = xmlHelper.makeXml(note, currentDate);
        String fileName = note.getTitle() + "-" + note.getId()+".xml";
        Path dir = Paths.get("imports", currentDate);
        Path pathToFile = dir.resolve(fileName);

        try {
            if (Files.notExists(pathToFile)) {
                Files.createDirectories(dir);
                Files.createFile(pathToFile);
            }
            Files.writeString(pathToFile, xml, StandardCharsets.UTF_8);
            log.info("Файл xml сохранен");
        } catch (IOException e) {
            log.error("Ошибка при записи в файл", e);
        }
    }
}
