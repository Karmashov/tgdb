package com.wwd.tgdb.bot;

import com.wwd.tgdb.dto.QuestionResponse;
import com.wwd.tgdb.dto.Response;
import lombok.Data;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class Bot extends TelegramLongPollingBot {

    private final int RECONNECT_PAUSE = 10000;

    private String userName;
	private String token;

    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();

    public Bot() {
    }

    @Override
    public void onUpdateReceived(Update update) {
        receiveQueue.add(update);
    }

    @Override
    public String getBotUsername() {
        return userName;
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

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        this.sendQueue.add(message);
    }

    public void sendReply(Response response) {
        SendMessage message = new SendMessage();
        message.setChatId(response.getChatId());
        message.setText(response.getMessage());
        message.setReplyToMessageId(response.getMessageId());

        if (response instanceof QuestionResponse) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int i = 0; i < ((QuestionResponse) response).getOptions().length; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton()
                        .setText(((QuestionResponse) response).getOptions()[i]);
                if (i < ((QuestionResponse) response).getCallbackData().length) {
                    button.setCallbackData(((QuestionResponse) response).getPosition() + ((QuestionResponse) response).getCallbackData()[i]);
                } else {
                    button.setCallbackData(((QuestionResponse) response).getOptions()[i]);
                }
                buttons.add(button);
            }
//            for (String option : ((QuestionResponse) response).getOptions()) {
//                buttonsRow1.add(new InlineKeyboardButton().setText(option).setCallbackData(((QuestionResponse) response).getPosition() + option));
//            }
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            rows.add(buttons);
            keyboard.setKeyboard(rows);
            message.setReplyMarkup(keyboard);
        }
        this.sendQueue.add(message);
    }
}
