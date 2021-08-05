package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.GPL.GPLUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GPLUploadRepository extends JpaRepository<GPLUpload, Integer> {

//    @Query(value = "SELECT u From GPLUpload u ORDER BY u.id DESC")
//    List<GPLUpload> findLastUpload();

    GPLUpload findTopByOrderByIdDesc();
}
