package com.wwd.tgdb.repository;

import com.wwd.tgdb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsUserByUserId(String name);

    User findFirstByUserId(String name);

//    Optional<User> findFirstByUserId(String userId);
}
