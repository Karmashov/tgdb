package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.dto.Response;
import com.wwd.tgdb.exception.EntityNotFoundException;
import com.wwd.tgdb.exception.UnknownCommandException;
import com.wwd.tgdb.repository.GPLUploadRepository;
import com.wwd.tgdb.service.FileService;
import com.wwd.tgdb.service.MessageService;
import com.wwd.tgdb.service.PriceService;
import com.wwd.tgdb.service.SOService;
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
    private final FileService fileService;
    private final UsdRateServiceImpl rateService;
    private final GPLUploadRepository uploadRepository;
    private final SOService soService;

    @Autowired
    public MessageServiceImpl(Bot bot,
                              PriceService priceService,
                              FileService fileService,
                              UsdRateServiceImpl rateService,
                              GPLUploadRepository uploadRepository,
                              SOService soService) {
        this.bot = bot;
        this.priceService = priceService;
        this.fileService = fileService;
        this.rateService = rateService;
        this.uploadRepository = uploadRepository;
        this.soService = soService;
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
        } catch (UnknownCommandException ex) {
            ex.setTgMessage(message);
            ex.printStackTrace();
            response = new Response(message.getChatId(), ex.getMessage(),  message.getMessageId());
        }

        sendReply(response);
    }

    private Response doLogic(String[] command) throws EntityNotFoundException, UnknownCommandException {
        switch (command[0]) {
            case "/so": {
                return soService.getSerials(command[1]);
            }
            case "/price": {
                return priceService.getPrice(command[1],
                        //TODO а если придет строка в command[2]?
                        command.length > 2 ? Integer.parseInt(command[2].replaceAll("[^\\d-.]", "")) : 0,
                        command.length > 3 ?
                                command[3].equalsIgnoreCase("завтра") ?
                                        LocalDate.now().plusDays(1) : LocalDate.now()
                                : null);
            }
            case "/rates": {
                return rateService.downloadRates();
            }
            case "/gpl": {
                return new Response("GPL был загружен: " +
                        uploadRepository.findTopByOrderByIdDesc().getUploadDate().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));
            }
            case "/checkgpl": {
                return fileService.checkGPL();
            }
//                case "/get": {
//                    //TODO получение товара
//                    System.out.println("В методе /get");
//                    break;
//                }
//                case "/send": {
//                    //TODO получение товара
//                    System.out.println("В методе /send");
//                    break;
//                }
            case "/тут?": {
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
        throw new UnknownCommandException();
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
        if (arr.length - 1 < position) {
            command = Arrays.copyOf(arr, position + 1);
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
}
