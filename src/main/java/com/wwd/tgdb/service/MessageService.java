package com.wwd.tgdb.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageService {

    void getMessage(Message message);

    void solveProblem(CallbackQuery query);
}
