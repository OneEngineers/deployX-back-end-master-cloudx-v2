package com.api.cloudx.repository;

import com.api.cloudx.entities.UserEntities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntities, Long> {

    Optional<UserEntities> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByName(String name);
}
