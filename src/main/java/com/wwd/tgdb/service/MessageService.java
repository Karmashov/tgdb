package com.wwd.tgdb.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageService {

    void getMessage(Update update);
}
