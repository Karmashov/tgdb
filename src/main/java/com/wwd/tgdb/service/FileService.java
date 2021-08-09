package com.wwd.tgdb.service;

import com.wwd.tgdb.dto.Response;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface FileService {

    Response getFile(Update update);
}
