package com.chanthea.backend.service;

import com.chanthea.backend.model.Deploy;
import java.util.List;

public interface DeployService {
    Deploy startDeployment(Deploy request);
    List<Deploy> getAllDeployments();
    Deploy getDeploymentById(Long id);
    Deploy updateStatus(Long id, String status, String imageTag);
}