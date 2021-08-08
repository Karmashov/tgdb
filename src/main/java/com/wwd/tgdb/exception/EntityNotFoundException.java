package com.wwd.tgdb.exception;

public class EntityNotFoundException extends RuntimeException {

    private static String message;
    private static long chatId;
    private static int messageId;

//    public EntityNotFoundException(String message) {
//        super(message);
//    }

    public EntityNotFoundException(String message, long chatId, int messageId) {
        super(message);
        this.chatId = chatId;
    }

//    public static EntityNotFoundException create() {
//        return new EntityNotFoundException(message);
//    }
}
