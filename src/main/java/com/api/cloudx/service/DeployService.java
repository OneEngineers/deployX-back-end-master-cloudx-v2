package com.api.cloudx.service;

import com.api.cloudx.entities.DeployEntities;

import java.util.List;

public interface DeployService {
    DeployEntities startDeployment(DeployEntities request);
    List<DeployEntities> getAllDeployments();
    DeployEntities getDeploymentById(Long id);
    DeployEntities updateStatus(Long id, String status, String imageTag);
}