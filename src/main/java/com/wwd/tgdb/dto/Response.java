package com.wwd.tgdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private long chatId;
    private String message;
    private int messageId;

    public Response(String message) {
        this.message = message;
    }
}
