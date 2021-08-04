package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.service.MessageService;
import com.wwd.tgdb.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MessageServiceImpl implements MessageService {

    private final Bot bot;
    private final PriceService priceService;
    private final UsdRateService rateService;

    @Autowired
    public MessageServiceImpl(Bot bot,
                              PriceService priceService,
                              UsdRateService rateService) {
        this.bot = bot;
        this.priceService = priceService;
        this.rateService = rateService;
    }

    @Override
    public void getMessage(Update update) {
        String receivedMessage = update.getMessage().getText();

        String[] command = receivedMessage.split(" ");
        String chatId = update.getMessage().getChatId().toString();
        switch (command[0]) {
            case "/price": {
                if (command.length == 3){
                    bot.sendMessage(chatId,
                            "Цена со скидкой " + command[2] + "%: " + priceService.getPriceWithDiscount(command[1], command[2]));
                } else {
                    bot.sendMessage(chatId, "GPL: " + priceService.getPrice(command[1]));
                }
                break;
            }
            case "/currency": {
                bot.sendMessage(chatId, rateService.getRates());
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
//            case "/pricerub": {
//                if (command.length == 4){
////                        rateService.getRates(chatId);
//                    priceServiceImpl.getPriceRub(command[1], command[2], command[3], chatId);
//                }
//                break;
//            }
//            case "/rates": {
//                rateService.getRates(chatId);
//            }
//            case "/sendstatforto":
//                adminService.sendStat(command[1], command[2], chatId);
//                break;
        }
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
