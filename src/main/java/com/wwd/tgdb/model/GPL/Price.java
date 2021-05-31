package com.wwd.tgdb.model.GPL;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "gpl")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String partnumber;

    private String vendor;

    private Double priceUsd;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
