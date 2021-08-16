package com.wwd.tgdb.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnauthorizedAccessException extends RuntimeException {
    private static String message = "Несанкционированный доступ";
    private Message tgMessage;

    public UnauthorizedAccessException() {
        super(message);
    }

    public UnauthorizedAccessException(Message tgMessage) {
        super(message);
        this.tgMessage = tgMessage;
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public static UnauthorizedAccessException create() {
        return new UnauthorizedAccessException(message);
    }
}
