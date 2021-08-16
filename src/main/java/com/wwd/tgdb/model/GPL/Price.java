package com.wwd.tgdb.model.GPL;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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

    private LocalDate changed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "partnumber", cascade = CascadeType.ALL)
    private List<RusSO> serials;
}
