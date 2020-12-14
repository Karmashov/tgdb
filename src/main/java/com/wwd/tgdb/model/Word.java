package com.wwd.tgdb.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String word;

    private Integer count;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
}
