package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.Alias;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AliasRepository extends JpaRepository<Alias, Integer> {

    boolean existAliasByAlias(String alias);

    Alias findFirstByAlias(String alias);
}
