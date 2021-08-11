package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.GPL.Price;
import com.wwd.tgdb.model.GPL.RusSO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RusSORepository extends JpaRepository<RusSO, Integer> {

    boolean existsBySerial(String serial);

    List<RusSO> findAllByPartnumber(Price partnumber);
}
