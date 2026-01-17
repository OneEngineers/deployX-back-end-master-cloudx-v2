package com.chanthea.backend.service;

import com.chanthea.backend.dto.DeployDTO;
import com.chanthea.backend.model.Deploy;

import java.util.List;

public interface DeployService {
    Deploy initiateDeployment(DeployDTO deployDTO);
    List<Deploy> getAllDeployments();
    Deploy getDeploymentById(Long id);
    void updateStatus(Long id, String status, String imageTag);
    void deleteDeployment(Long id);
}