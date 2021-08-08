package com.wwd.tgdb.service;

import com.wwd.tgdb.dto.Response;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageService {

    void getMessage(Message message);

    void solveProblem(CallbackQuery query);

    void sendReply(Response response);
}
