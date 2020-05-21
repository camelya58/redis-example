package com.github.camelya58.redisexample.transfer;

import com.github.camelya58.redisexample.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Класс XmlHelper задает параментры для работы с шаблоном
 *
 *  @author Kamila Meshcheryakova
 */
@Component
@RequiredArgsConstructor
public class XmlHelper {

    private final TemplateEngine templateEngine;

    /**
     *  Подстановка изменяемых полей в шаблон и формирование xml файла
     *
     * @param note экземпляр входящего запроса
     * @param date дата формирования xml файла
     * @return сформированный по шаблону xml файл в виде строки
     */
    public String makeXml(Note note, String date) {
        Context context = new Context();
        context.setVariable("noteId", note.getId());
        context.setVariable("authorName", note.getAuthor());
        context.setVariable("title", note.getTitle());
        context.setVariable("content", note.getContent());
        context.setVariable("date", date);
        return templateEngine.process("NoteTemplate.xml", context);
    }
}
