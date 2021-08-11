package com.wwd.tgdb.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnknownCommandException extends RuntimeException {

    private static String message = "Неизвестная команда";
    private Message tgMessage;

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
