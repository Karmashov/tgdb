package com.wwd.tgdb.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class EntityNotFoundException extends RuntimeException {

    private static String message = "Не найдено";
    private Message tgMessage;

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
