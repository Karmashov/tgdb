package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.GPL.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Integer> {

    boolean existsByPartnumber(String name);

    Price findFirstByPartnumber(String name);

    boolean existsByPartnumberContaining(String substring);

    List<Price> findByPartnumberContaining(String substring);

    @Override
    void deleteAll();
}
