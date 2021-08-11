package com.wwd.tgdb.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "aliases")
public class Alias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String alias;

    private String replacement;
}
