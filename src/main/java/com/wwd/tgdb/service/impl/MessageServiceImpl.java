package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.repository.GPLUploadRepository;
import com.wwd.tgdb.service.MessageService;
import com.wwd.tgdb.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;

@Service
public class MessageServiceImpl implements MessageService {

    private final Bot bot;
    private final PriceService priceService;
    private final UsdRateServiceImpl rateService;
    private final GPLUploadRepository uploadRepository;

    @Autowired
    public MessageServiceImpl(Bot bot,
                              PriceService priceService,
                              UsdRateServiceImpl rateService,
                              GPLUploadRepository uploadRepository) {
        this.bot = bot;
        this.priceService = priceService;
        this.rateService = rateService;
        this.uploadRepository = uploadRepository;
    }

    @Override
    public void getMessage(Message message) {
        String receivedMessage = message.getText();

        String[] command;
        if (receivedMessage.contains("\n")) {
            command = receivedMessage.split("\n");
        } else {
            command = receivedMessage.split(" ");
        }

        String chatId = message.getChatId().toString();
        //@TODO вынести в отдельный метод с сигнатурой (String command)
        switch (command[0]) {
            case "/price": {
                if (command.length == 3){
                    bot.sendMessage(chatId,
                            "Цена " + command[1].toUpperCase() + " со скидкой " + command[2] + "%: $" +
                                    priceService.getPriceWithDiscount(command[1], command[2]));
                } else {
                    bot.sendMessage(chatId, "GPL " + command[1].toUpperCase() + ": $" + priceService.getPrice(command[1]));
                }
                break;
            }
            case "/pricerub": {
                if (command.length == 4){
                    bot.sendMessage(chatId,
                            "Цена " + command[1].toUpperCase() + " со скидкой " + command[2] + "%, по курсу на завтра: ₽" +
                                    priceService.getPriceRub(command[1], command[2], command[3]));
                } else if (command.length == 3) {
                    bot.sendMessage(chatId,
                            "Цена " + command[1].toUpperCase() + " со скидкой " + command[2] + "%, по курсу на сегодня: ₽" +
                                    priceService.getPriceRub(command[1], command[2], ""));
                }
                break;
            }
            case "/rates": {
                bot.sendMessage(chatId, rateService.downloadRates());
                break;
            }
            case "/gpl": {
                bot.sendMessage(chatId, "GPL был загружен: " +
                        uploadRepository.findTopByOrderByIdDesc().getUploadDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                break;
            }
            case "/get": {
                //@TODO получение товара
                System.out.println("В методе /get");
                break;
            }
            case "/send": {
                //@TODO получение товара
                System.out.println("В методе /send");
                break;
            }
            case "/тут?": {
                bot.sendMessage(chatId, "Я на месте");
                break;
            }
//            case "/chatid":
//                adminService.sendChatId(chatId);
//                break;
//            case "/chats":
//                adminService.getChats(chatId);
//                break;
//            case "/sendstatforto":
//                adminService.sendStat(command[1], command[2], chatId);
//                break;
        }
    }

    @Override
    public void solveProblem(CallbackQuery query) {
        String message = query.getMessage().getReplyToMessage().getText();
        String[] command = message.split(" ");
        int position = Integer.parseInt(String.valueOf(query.getData().charAt(0)));
        command[position] = new StringBuilder(query.getData()).deleteCharAt(0).toString();

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(query.getMessage().getChatId());
        deleteMessage.setMessageId(query.getMessage().getMessageId());
        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        //@TODO вызов логики
    }

    //    public void parseText(Update update) {
//        Chat chat = chatRepository.findFirstByChatId(update.getMessage().getChatId().toString());
//
//        User user = userRepository.findFirstByUserId(update.getMessage().getFrom().getId().toString());
//
//        LocalDateTime timestamp = parseDate(update.getMessage().getDate());
//
//        String text = EmojiParser.removeAllEmojis(update.getMessage().getText());
//        if (!text.equals("")) {
//            writeMessage(text, chat, user, timestamp);
//        }
//    }

//    private LocalDateTime parseDate(Integer date) {
//        LocalDateTime ldt = Instant.ofEpochSecond(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
//        return ldt;
//    }

//    private User addUser(Update update) {
//        User user = new User();
//        user.setUserId(update.getMessage().getFrom().getId().toString());
//        user.setUsername(update.getMessage().getFrom().getUserName());
//        userRepository.save(user);
//        return user;
//    }
//
//    private Chat addChat(Update update) {
//        Chat chat = new Chat();
//        chat.setTitle(update.getMessage().getChat().getTitle());
//        chat.setUsername(update.getMessage().getChat().getUserName());
//        chat.setChatId(update.getMessage().getChatId().toString());
//        chatRepository.save(chat);
//
//        return chat;
//    }

//    private void writeMessage(String text, Chat chat, User user, LocalDateTime timestamp) {
//        Message message = new Message();
//        message.setText(text);
//        message.setChat(chat);
//        message.setUser(user);
//        message.setTime(timestamp);
//        messageRepository.save(message);
//        System.out.println(LocalDateTime.now());
//    }
}
