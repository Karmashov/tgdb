package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    boolean existsChatByChatId(String name);

    Chat findFirstByChatId(String name);

//    Optional<Chat> findFirstByChatId(String chatId);
}
