package com.wwd.tgdb.model;

import com.wwd.tgdb.model.enumerated.ChatType;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private ChatType chatType;

    private String title;

    private String username;

    private String chatId;

    private String inviteLink;

    private Boolean recordOn;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    @OneToMany(mappedBy = "chat")
    private List<Word> words;
}
