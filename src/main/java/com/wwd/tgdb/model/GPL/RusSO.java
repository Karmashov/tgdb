package com.wwd.tgdb.model.GPL;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "sales_orders")
public class RusSO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpl_id")
    private Price partnumber;

    private String serial;

    private String customer;
}
