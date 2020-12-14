package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.model.Chat;
import com.wwd.tgdb.model.Message;
import com.wwd.tgdb.model.Word;
import com.wwd.tgdb.repository.ChatRepository;
import com.wwd.tgdb.repository.MessageRepository;
import com.wwd.tgdb.repository.UserRepository;
import com.wwd.tgdb.repository.WordRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class AdminService {

    private Bot bot;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final JavaMailSender mailSender;

    public AdminService(Bot bot,
                        ChatRepository chatRepository,
                        MessageRepository messageRepository,
                        UserRepository userRepository,
                        WordRepository wordRepository,
                        JavaMailSender mailSender) {
        this.bot = bot;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.mailSender = mailSender;
    }

    public void sendChatId(String chatId){
        sendMessage(chatId, chatId);
    }

    public void countWords(String chatId) {
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            String[] words = message.getText().split(" ");
            for (String word : words) {
                word = word.replaceAll("[^A-Za-zА-Яа-я0-9_]", "");
                if (word.equals("")) {
                    continue;
                }
                if (!wordRepository.existsByWordAndChat(word, chatRepository.findFirstByChatId(message.getChat().getChatId()))) {
                    addWord(word, message.getChat().getChatId());
                } else {
                    updateCount(word, chatRepository.findFirstByChatId(message.getChat().getChatId()));
                }
            }
        }
        sendMessage(chatId,"Complete");
    }

    private void updateCount(String word, Chat chat) {
        Word newWord = wordRepository.findFirstByWordAndChat(word, chat);
        newWord.setCount(newWord.getCount() + 1);
        wordRepository.save(newWord);
    }

    private void addWord(String word, String chatId) {
        Word newWord = new Word();
        newWord.setWord(word);
        newWord.setCount(1);
        newWord.setChat(chatRepository.findFirstByChatId(chatId));
        wordRepository.save(newWord);
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

    public void clearWords(String chatId) {
        wordRepository.deleteAll();
        sendMessage(chatId,"Complete");
    }

    public void wordStat(String chatId) {

    }

    public void wordStatFor(String request, String chatId) {
        Chat chat = chatRepository.findFirstByChatId(request);
        List<Word> result = wordRepository.findAllByChatOrderByCountDesc(chat);
        StringBuilder message = new StringBuilder();
        String chatName = chat.getUsername() == null ? chat.getTitle() : chat.getUsername();
        message.append(chatName).append("\n");
        for (Word word : result) {
            message.append(word.getWord()).append("\t - ").append(word.getCount()).append("\t").append("\n");
        }
        sendMessage(chatId, message.toString());
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        bot.sendQueue.add(message);
    }

    public void sendStat(String request, String mailTo, String chatId) {
        Chat chat = chatRepository.findFirstByChatId(request);
        List<Word> result = wordRepository.findAllByChatOrderByCountDesc(chat);

        MailService mailService = new MailService(mailSender);
        mailService.excelCreation(result, mailTo);
    }
}
