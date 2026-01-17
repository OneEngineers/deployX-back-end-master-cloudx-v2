package com.chanthea.backend.repository;

import com.chanthea.backend.model.Deploy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeployRepository extends JpaRepository<Deploy, Long> {
    Optional<Deploy> findByProjectName(String projectName);
}

