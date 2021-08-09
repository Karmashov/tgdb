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

    public void sendReply(Response response) {
        SendMessage message = new SendMessage();
        message.setChatId(response.getChatId());
        message.setText(response.getMessage());
        message.setReplyToMessageId(response.getMessageId());

        if (response instanceof QuestionResponse) {
            InlineKeyboardMarkup keyboard = createKeyboard((QuestionResponse) response);
            message.setReplyMarkup(keyboard);
        }
        this.sendQueue.add(message);
    }

    private InlineKeyboardMarkup createKeyboard(QuestionResponse response) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (int i = 0; i < response.getOptions().length; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton()
                    .setText(response.getOptions()[i]);
            if (i < response.getCallbackData().length) {
                button.setCallbackData(response.getPosition() + response.getCallbackData()[i]);
            } else {
                button.setCallbackData(response.getOptions()[i]);
            }
            buttons.add(button);
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttons);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
}
