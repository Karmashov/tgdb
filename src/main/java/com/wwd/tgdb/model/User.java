package com.wwd.tgdb.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String userId;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

//    @OneToMany(mappedBy = "user")
//    private List<Word> words;
}
