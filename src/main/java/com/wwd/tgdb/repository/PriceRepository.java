package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.GPL.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Integer> {

    boolean existsByPartnumber(String name);

    Price findFirstByPartnumber(String name);

    @Override
    void deleteAll();
}
