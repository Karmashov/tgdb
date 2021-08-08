package com.wwd.tgdb.exception;

import com.wwd.tgdb.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    private final MessageService messageService;

    @Autowired
    public ExceptionController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
//        System.out.println("popal");
//        messageService.sendReply(new Error(ex.getMessage(), ex.getChatId(), ex.getMessageId()));
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(MaxUploadSizeException.class)
//    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeException ex) {
//        FileExceptionHandler handler = new FileExceptionHandler(ex.getMessage());
//
//        return new ResponseEntity<>(handler, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(IllegalFormatException.class)
//    public ResponseEntity<Object> handleIllegalFormatException(IllegalFormatException ex) {
//        FileExceptionHandler handler = new FileExceptionHandler(ex.getMessage());
//
//        return new ResponseEntity<>(handler, HttpStatus.BAD_REQUEST);
//    }
}
