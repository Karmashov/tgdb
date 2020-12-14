package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.Chat;
import com.wwd.tgdb.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Integer> {

    boolean existsByWordAndChat(String word, Chat chatId);

    Word findFirstByWordAndChat(String word, Chat chatId);

    List<Word> findAllByChatOrderByCountDesc(Chat chat);
}
