package com.github.camelya58.redisexample.repository;

import com.github.camelya58.redisexample.model.Note;
import com.github.camelya58.redisexample.transfer.XmlHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.*;

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
        File file = new File("imports/" + currentDate + "/note-" + note.getId()+".xml");
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            log.error("Ошибка при создании файла", e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(xml);
            log.info("Файл xml сохранен");
        } catch (FileNotFoundException e) {
            log.error("Файл не найден", e);
        } catch(IOException e) {
            log.error("Ошибка при записи в файл", e);
        }
    }
}
