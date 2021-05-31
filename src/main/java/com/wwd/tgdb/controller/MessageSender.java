package com.wwd.tgdb.controller;

import com.wwd.tgdb.bot.Bot;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
public class MessageSender implements Runnable{

    private final int SENDER_SLEEP_TIME = 1000;
    private static final int PRIORITY_FOR_SENDER = 1;

    private final Bot bot;

    public MessageSender(Bot bot) {
        this.bot = bot;

        Thread sender = new Thread(this);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(PRIORITY_FOR_SENDER);
        sender.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (Object object = bot.sendQueue.poll(); object != null; object = bot.sendQueue.poll()) {
                    send(object);
                }
                try {
                    Thread.sleep(SENDER_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(Object object){
        try {
            BotApiMethod<Message> message = (BotApiMethod<Message>) object;
            bot.execute(message);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}
