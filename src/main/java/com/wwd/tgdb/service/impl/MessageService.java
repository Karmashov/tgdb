package com.wwd.tgdb.service.impl;

import com.vdurmont.emoji.EmojiParser;
import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.model.Chat;
import com.wwd.tgdb.model.Message;
import com.wwd.tgdb.model.User;
import com.wwd.tgdb.repository.ChatRepository;
import com.wwd.tgdb.repository.MessageRepository;
import com.wwd.tgdb.repository.UserRepository;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MessageService {

    private Bot bot;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(Bot bot,
                          ChatRepository chatRepository,
                          MessageRepository messageRepository,
                          UserRepository userRepository) {
        this.bot = bot;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void parseText(Update update) {
        Chat chat = chatRepository.existsChatByChatId(update.getMessage().getChatId().toString()) ?
                chatRepository.findFirstByChatId(update.getMessage().getChatId().toString()) :
                addChat(update);

        User user = userRepository.existsUserByUserId(update.getMessage().getFrom().getId().toString()) ?
                userRepository.findFirstByUserId(update.getMessage().getFrom().getId().toString()) :
                addUser(update);

        LocalDateTime timestamp = parseDate(update.getMessage().getDate());

        String text = EmojiParser.removeAllEmojis(update.getMessage().getText());
        if (!text.equals("")) {
            writeMessage(text, chat, user, timestamp);
        }
    }

    private LocalDateTime parseDate(Integer date) {
        LocalDateTime ldt = Instant.ofEpochSecond(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldt;
    }

    private User addUser(Update update) {
        User user = new User();
        user.setUserId(update.getMessage().getFrom().getId().toString());
        user.setUsername(update.getMessage().getFrom().getUserName());
        userRepository.save(user);
        return user;
    }

    private Chat addChat(Update update) {
        Chat chat = new Chat();
        chat.setTitle(update.getMessage().getChat().getTitle());
        chat.setUsername(update.getMessage().getChat().getUserName());
        chat.setChatId(update.getMessage().getChatId().toString());
        chatRepository.save(chat);

        return chat;
    }

    private void writeMessage(String text, Chat chat, User user, LocalDateTime timestamp) {
        Message message = new Message();
        message.setText(text);
        message.setChat(chat);
        message.setUser(user);
        message.setTime(timestamp);
        messageRepository.save(message);
        System.out.println(LocalDateTime.now());
    }
}
