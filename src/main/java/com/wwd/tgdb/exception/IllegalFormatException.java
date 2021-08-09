package com.wwd.tgdb.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class IllegalFormatException extends RuntimeException {
    private static String message = "Неверный формат файла";
    private Message tgMessage;

    public IllegalFormatException() {
        super(message);
    }

    public IllegalFormatException(Message tgMessage) {
        super(message);
        this.tgMessage = tgMessage;
    }

    public IllegalFormatException(String message) {
        super(message);
    }

    public static IllegalFormatException create() {
        return new IllegalFormatException(message);
    }
}
