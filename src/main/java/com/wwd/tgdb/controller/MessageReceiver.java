package com.wwd.tgdb.controller;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.dto.Response;
import com.wwd.tgdb.exception.IllegalFormatException;
import com.wwd.tgdb.exception.UnauthorizedAccessException;
import com.wwd.tgdb.model.User;
import com.wwd.tgdb.model.enumerated.UserRole;
import com.wwd.tgdb.repository.UserRepository;
import com.wwd.tgdb.service.FileService;
import com.wwd.tgdb.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class MessageReceiver implements Runnable{

    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    private final Bot bot;
    private final FileService fileService;
    private UserRepository userRepository;
    private final MessageService messageService;

    @Autowired
    public MessageReceiver(Bot bot,
                           UserRepository userRepository,
                           FileService fileService,
                           MessageService messageService) {
        this.bot = bot;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.messageService = messageService;

        Thread receiver = new Thread(this);
        receiver.setDaemon(true);
        receiver.setName("MsgReceiver");
        receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();
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
        if (object instanceof Update) {
            Update update = (Update) object;
            try {
                User user;

                if (update.hasCallbackQuery()) {
                    if (!update.getCallbackQuery().getData().equalsIgnoreCase("нет")) {
                        messageService.solveProblem(update.getCallbackQuery());
                    } else {
                        messageService.deleteKeyboard(
                                update.getCallbackQuery().getMessage().getChatId(),
                                update.getCallbackQuery().getMessage().getMessageId()
                        );
                        messageService.editMessageText(
                                update.getCallbackQuery().getMessage().getChatId(),
                                update.getCallbackQuery().getMessage().getMessageId(),
                                update.getCallbackQuery().getMessage().getText().substring(
                                        0, update.getCallbackQuery().getMessage().getText().indexOf("\n")
                                )
                        );
                    }
                } else {
                    user = userRepository.existsUserByUserId(update.getMessage().getFrom().getId().toString()) ?
                            userRepository.findFirstByUserId(update.getMessage().getFrom().getId().toString()) : addUser(update);

                    if (update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
                        if (!user.getUserRole().equals(UserRole.UNKNOWN)) {
                            messageService.getMessage(update.getMessage());
                        } else {
                            throw new UnauthorizedAccessException();
                        }
                    } else if (update.getMessage().hasDocument()) {
                        if (user.getUserRole().equals(UserRole.ADMIN)) {
                            fileService.getFile(update);
                        } else {
                            throw new UnauthorizedAccessException();
                        }
                    }
                }
            } catch (IllegalFormatException ex) {
                ex.setTgMessage(update.getMessage());
                ex.printStackTrace();
                bot.sendReply(new Response(update.getMessage().getChatId(), ex.getMessage(),  update.getMessage().getMessageId()));
            } catch (UnauthorizedAccessException ex) {
                ex.setTgMessage(update.getMessage());
                ex.printStackTrace();
                bot.sendReply(new Response(update.getMessage().getChatId(), ex.getMessage(),  update.getMessage().getMessageId()));
            } catch (NullPointerException ex) {
                ex.printStackTrace();
                bot.sendReply(new Response(update.getMessage().getChatId(), ex.getMessage(),  update.getMessage().getMessageId()));
            }
        } else {
            System.out.println(object.toString());
        }
    }

    private User addUser(Update update) {
        User user = new User();
        user.setUserId(update.getMessage().getFrom().getId().toString());
        user.setUsername(update.getMessage().getFrom().getUserName());
        user.setUserRole(UserRole.UNKNOWN);
        userRepository.save(user);
        return user;
    }

//    private Chat addChat(Update update) {
//        Chat chat = new Chat();
//        chat.setTitle(update.getMessage().getChat().getTitle());
//        chat.setUsername(update.getMessage().getChat().getUserName());
//        chat.setChatId(update.getMessage().getChatId().toString());
//        chatRepository.save(chat);
//
//        return chat;
//    }
}
