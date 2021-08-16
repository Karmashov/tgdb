package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.GPL.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);

    Category findFirstBySectionId(String categoryId);

    @Override
    void deleteAll();
}
