package com.wwd.tgdb.service;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.model.Chat;
import com.wwd.tgdb.model.User;
import com.wwd.tgdb.repository.ChatRepository;
import com.wwd.tgdb.repository.UserRepository;
import com.wwd.tgdb.service.impl.AdminService;
import com.wwd.tgdb.service.impl.MessageService;
import com.wwd.tgdb.service.impl.PriceService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageReceiver implements Runnable{

    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
    private final ChatRepository chatRepository;
    private  final UserRepository userRepository;
    private final AdminService adminService;
    private final MessageService messageService;
    private final PriceService priceService;

    private Bot bot;

    public MessageReceiver(Bot bot,
                           ChatRepository chatRepository,
                           UserRepository userRepository,
                           AdminService adminService,
                           MessageService messageService,
                           PriceService priceService) {
        this.bot = bot;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.adminService = adminService;
        this.messageService = messageService;
        this.priceService = priceService;
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
            if (!chatRepository.existsChatByChatId(update.getMessage().getChatId().toString())) {
                addChat(update);
            }
            if (!userRepository.existsUserByUserId(update.getMessage().getFrom().getId().toString())) {
                addUser(update);
            }
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
                case "/chats":
                    adminService.getChats(chatId);
                    break;
                case "/price":
                    if (command.length == 3){
                        priceService.getPriceWithDiscount(command[1], command[2], chatId);
                    } else {
                        priceService.getPrice(command[1], chatId);
                    }
                    break;
//                case "/sendstatforto":
//                    adminService.sendStat(command[1], command[2], chatId);
//                    break;
            }
        } else {
            messageService.parseText(update);
        }
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
}
