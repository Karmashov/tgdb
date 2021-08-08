package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.GPL.GPLUpload;
import com.wwd.tgdb.model.UsdRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UsdRateRepository extends JpaRepository<UsdRate, Integer> {

    boolean existsByDate(LocalDate date);

    UsdRate findFirstByDate(LocalDate date);

    UsdRate findTopByOrderByIdDesc();
}
