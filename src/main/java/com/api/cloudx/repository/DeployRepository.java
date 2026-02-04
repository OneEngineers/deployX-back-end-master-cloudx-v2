package com.api.cloudx.repository;

import com.api.cloudx.entities.DeployEntities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeployRepository extends JpaRepository<DeployEntities, Long> {
//    Optional<DeployEntities> findByProjectName(String projectName);
}

