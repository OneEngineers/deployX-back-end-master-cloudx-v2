package com.api.cloudx.repository;

import com.api.cloudx.model.Deploy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeployRepository extends JpaRepository<Deploy, Long> {
//    Optional<Deploy> findByProjectName(String projectName);
}

