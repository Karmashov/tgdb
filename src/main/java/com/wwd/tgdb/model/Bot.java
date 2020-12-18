package com.wwd.tgdb.model;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//import com.wwd.tgadwards.service.impl.AdminServiceImpl;
//import com.wwd.tgadwards.service.impl.MessageServiceImpl;


public class Bot extends TelegramLongPollingBot {

    private final int RECONNECT_PAUSE = 10000;

    private String username;
    private String token;

    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();

//    @Autowired
//    public Bot(BotController botController) {
//        this.botController = botController;
//    }
//
    public Bot(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public void onUpdateReceived(Update update) {
//        System.out.println(LocalDateTime.now());
//        System.out.println(update.getMessage().getText());
        receiveQueue.add(update);
//        BotController botController = new BotController();
//        botController.controller(update);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return;
            }
            botConnect();
        }
    }
}
