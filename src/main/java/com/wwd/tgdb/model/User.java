package com.wwd.tgdb.model;

import com.wwd.tgdb.model.enumerated.UserRole;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String userId;

    @Enumerated(value = EnumType.STRING)
    private UserRole userRole;

    private String email;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

//    @OneToMany(mappedBy = "user")
//    private List<Word> words;
}
