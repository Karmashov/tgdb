package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
