package com.wwd.tgdb.service;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.service.impl.AdminService;
import com.wwd.tgdb.service.impl.MessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageReceiver implements Runnable{

    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
    private final AdminService adminService;
    private final MessageService messageService;

    private Bot bot;

    public MessageReceiver(Bot bot, AdminService adminService, MessageService messageService) {
        this.bot = bot;
        this.adminService = adminService;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                analyze(object);
            }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void analyze(Object object) {
        if (object instanceof Update &&
            ((Update) object).getMessage().getText() != null) {
            Update update = (Update) object;
            analyzeUpdate(update);
        } else {
            System.out.println(object.toString());
        }
    }

    private void analyzeUpdate(Update update) {
        String receivedMessage = update.getMessage().getText();

        if (receivedMessage.charAt(0) == '/') {
            String[] command = receivedMessage.split(" ");
            String chatId = update.getMessage().getChatId().toString();
            switch (command[0]) {
                case "/chatid":
                    adminService.sendChatId(chatId);
                    break;
                case "/countwords":
                    adminService.countWords(chatId);
                    break;
                case "/clearwords":
                    adminService.clearWords(chatId);
                    break;
                case "/chats":
                    adminService.getChats(chatId);
                    break;
                case "/wordstat":
                    adminService.wordStat(chatId);
                    break;
                case "/wordstatfor":
                    adminService.wordStatFor(command[1], chatId);
                    break;
                case "/sendstatforto":
                    adminService.sendStat(command[1], command[2], chatId);
                    break;
            }
        } else {
            messageService.parseText(update);
        }
    }
}
