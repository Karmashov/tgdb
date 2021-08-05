package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.GPL.GPLUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GPLUploadRepository extends JpaRepository<GPLUpload, Integer> {

    GPLUpload findTopByIdDesc();
}
