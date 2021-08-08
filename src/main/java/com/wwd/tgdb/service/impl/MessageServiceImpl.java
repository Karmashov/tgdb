package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.dto.QuestionResponse;
import com.wwd.tgdb.dto.Response;
import com.wwd.tgdb.exception.EntityNotFoundException;
import com.wwd.tgdb.repository.GPLUploadRepository;
import com.wwd.tgdb.service.MessageService;
import com.wwd.tgdb.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
        generateResponse(command, message);
    }

    private void generateResponse(String[] command, Message message) {
        Response response;
        try {
            response = doLogic(command);
            response.setChatId(message.getChatId());
            response.setMessageId(message.getMessageId());
        } catch (EntityNotFoundException ex) {
            ex.setTgMessage(message);
            ex.printStackTrace();
            response = new Response(message.getChatId(), ex.getMessage(),  message.getMessageId());
        }
        sendReply(response);
    }

    private Response doLogic(String[] command) throws EntityNotFoundException {
        switch (command[0]) {
//                case "/test": {
//                    throwException(message);
//                    break;
//                }
            case "/price": {
                return priceService.getPriceWithDiscount(command[1],
                        //TODO а если придет строка в command[2]?
                        command.length > 2 ? Integer.parseInt(command[2].replaceAll("[^\\d-.]", "")) : 0,
                        command.length > 3 ?
                                command[3].equalsIgnoreCase("завтра") ?
                                        LocalDate.now().plusDays(1) : LocalDate.now()
                                : null);
//                    if (command.length == 3){
//                        bot.sendMessage(chatId,
//                                "Цена " + command[1].toUpperCase() + " со скидкой " + command[2] + "%: $" +
//                                        priceService.getPriceWithDiscount(command[1], command[2]));
//                    } else {
//                        bot.sendMessage(chatId, "GPL " + command[1].toUpperCase() + ": $" + priceService.getPrice(command[1]));
//                    }
//                    break;
            }
//                case "/pricerub": {
//                    if (command.length == 4){
//                        bot.sendMessage(chatId,
//                                "Цена " + command[1].toUpperCase() + " со скидкой " + command[2] + "%, по курсу на завтра: ₽" +
//                                        priceService.getPriceRub(command[1], command[2], command[3]));
//                    } else if (command.length == 3) {
//                        bot.sendMessage(chatId,
//                                "Цена " + command[1].toUpperCase() + " со скидкой " + command[2] + "%, по курсу на сегодня: ₽" +
//                                        priceService.getPriceRub(command[1], command[2], ""));
//                    }
//                    break;
//                }
            case "/rates": {
                return rateService.downloadRates();
            }
            case "/gpl": {
//                bot.sendMessage(chatId, "GPL был загружен: " +
//                        uploadRepository.findTopByOrderByIdDesc().getUploadDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                return new Response("GPL был загружен: " +
                        uploadRepository.findTopByOrderByIdDesc().getUploadDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
//                case "/get": {
//                    //@TODO получение товара
//                    System.out.println("В методе /get");
//                    break;
//                }
//                case "/send": {
//                    //@TODO получение товара
//                    System.out.println("В методе /send");
//                    break;
//                }
            case "/тут?": {
//                bot.sendMessage(chatId, "Я на месте");
                return new Response("Я на месте");
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
        return new Response("Неизвестная команда");
    }

    private void throwException(Message message) throws EntityNotFoundException {
        throw new EntityNotFoundException();
    }

    @Override
    public void solveProblem(CallbackQuery query) {
        String message = query.getMessage().getReplyToMessage().getText();

        String[] arr;
        if (message.contains("\n")) {
            arr = message.split("\n");
        } else {
            arr = message.split(" ");
        }

        String[] command;
        int position = Integer.parseInt(String.valueOf(query.getData().charAt(0)));
        System.out.println(arr.length + " - " + position);
        if (arr.length - 1 < position) {
            command = Arrays.copyOf(arr, position + 1);
            System.out.println(command.length);
        } else {
            command = message.split(" ");
        }
        command[position] = new StringBuilder(query.getData()).deleteCharAt(0).toString();

        deleteMessage(query.getMessage().getChatId(), query.getMessage().getMessageId());

        generateResponse(command, query.getMessage().getReplyToMessage());
    }

    @Override
    public void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteKeyboard(long chatId, int messageId) {
        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setReplyMarkup(null);
        try {
            bot.execute(edit);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editMessageText(long chatId, int messageId, String text) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText(text);
        try {
            bot.execute(edit);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendReply(Response response) {
        bot.sendReply(response);
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
