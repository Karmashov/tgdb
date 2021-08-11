package com.wwd.tgdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse extends Response {

    private String[] options;
    private String[] callbackData;
    private int position;

//    public QuestionResponse(String[] options, int position) {
//        this.options = options;
//        this.position = position;
//    }

//    public QuestionResponse(long chatId, String message, int messageId, String[] options, int position) {
//        super(chatId, message, messageId);
//        this.options = options;
//        this.position = position;
//    }

    public QuestionResponse(String message, String[] options, String[] callbackData, int position) {
        super(message);
        this.options = options;
        this.callbackData = callbackData;
        this.position = position;
    }
}
