package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.model.Chat;
import com.wwd.tgdb.repository.ChatRepository;
import com.wwd.tgdb.repository.MessageRepository;
import com.wwd.tgdb.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class AdminService {

    private Bot bot;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public AdminService(Bot bot,
                        ChatRepository chatRepository,
                        MessageRepository messageRepository,
                        UserRepository userRepository,
                        JavaMailSender mailSender) {
        this.bot = bot;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public void sendChatId(String chatId){
        sendMessage(chatId, chatId);
    }

    public void getChats(String chatId) {
        List<Chat> result = chatRepository.findAll();
        StringBuilder message = new StringBuilder();
        for (Chat chat : result) {
            String chatName = chat.getUsername() == null ? chat.getTitle() : chat.getUsername();
            message.append(chat.getChatId()).append("\t").append(chatName).append("\n");
        }
        sendMessage(chatId, message.toString());
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        bot.sendQueue.add(message);
    }

//    public void sendStat(String request, String mailTo, String chatId) {
//        Chat chat = chatRepository.findFirstByChatId(request);
////        List<Word> result = wordRepository.findAllByChatOrderByCountDesc(chat);
//
//        MailService mailService = new MailService(mailSender);
//        mailService.excelCreation(result, mailTo);
//    }
}
