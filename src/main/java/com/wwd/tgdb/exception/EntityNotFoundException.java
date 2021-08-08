package com.wwd.tgdb.exception;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
public class EntityNotFoundException extends RuntimeException {

    private static String message = "Не найдено";
    private Message tgMessage;
//    private long chatId;
//    private int messageId;

    public EntityNotFoundException() {
        super(message);
    }

    public EntityNotFoundException(Message tgMessage) {
        super(message);
        this.tgMessage = tgMessage;
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException create() {
        return new EntityNotFoundException(message);
    }
}
