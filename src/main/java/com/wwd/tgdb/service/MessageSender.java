package com.wwd.tgdb.service;

import com.wwd.tgdb.model.Bot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageSender implements Runnable{

    private final int SENDER_SLEEP_TIME = 1000;

    private Bot bot;

    public MessageSender(Bot bot) {
        this.bot = bot;
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
