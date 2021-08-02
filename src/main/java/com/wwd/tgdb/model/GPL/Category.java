package com.wwd.tgdb.model.GPL;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "gpl_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String sectionId;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Price> prices;
}
