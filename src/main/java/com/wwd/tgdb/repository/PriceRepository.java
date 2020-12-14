package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.Chat;
import com.wwd.tgdb.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Integer> {

    boolean existsByPartnumber(String name);

    Price findFirstByPartnumber(String name);
}
