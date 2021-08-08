package com.wwd.tgdb.exception;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
public class UnknownCommandException extends RuntimeException {

    private static String message = "Не найдено";
    private Message tgMessage;
//    private long chatId;
//    private int messageId;

    public UnknownCommandException() {
        super(message);
    }

    public UnknownCommandException(Message tgMessage) {
        super(message);
        this.tgMessage = tgMessage;
    }

    public UnknownCommandException(String message) {
        super(message);
    }

    public static UnknownCommandException create() {
        return new UnknownCommandException(message);
    }
}
