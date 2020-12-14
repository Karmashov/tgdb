//package com.wwd.tgadwards.controller;
//
//import com.wwd.tgadwards.service.AdminService;
//import com.wwd.tgadwards.service.MessageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//public class BotController {
//
//    private final AdminService adminService;
//    private final MessageService messageService;
//
//    @Autowired
//    public BotController(AdminService adminService, MessageService messageService) {
//        this.adminService = adminService;
//        this.messageService = messageService;
//    }
//
//    public void controller(Update update) {
//        String receivedMessage = update.getMessage().getText();
//
//        if (receivedMessage.charAt(0) == '/') {
//            System.out.println(receivedMessage);
//            String[] command = receivedMessage.split(" ");
//            switch (command[0]) {
//                case "/chatid":
//                    adminService.getChatId(update.getMessage().getChatId().toString());
//                    break;
//            }
//        } else {
//            System.out.println(receivedMessage);
//        }
//    }
//}
