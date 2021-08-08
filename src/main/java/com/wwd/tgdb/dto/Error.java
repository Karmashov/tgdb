package com.wwd.tgdb.dto;

import lombok.Data;

@Data
public class Error implements Response {

    private String message;
    private long chatId;
    private int messageId;

    public Error(String message, long chatId, int messageId) {
        this.message = message;
        this.chatId = chatId;
        this.messageId = messageId;
    }
}
