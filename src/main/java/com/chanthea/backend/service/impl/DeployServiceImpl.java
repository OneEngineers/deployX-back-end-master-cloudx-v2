package com.chanthea.backend.service.impl;

import com.chanthea.backend.dto.DeployDTO;
import com.chanthea.backend.exception.BusinessException;
import com.chanthea.backend.model.Deploy;
import com.chanthea.backend.repository.DeployRepository;
import com.chanthea.backend.service.DeployService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeployServiceImpl implements DeployService {

    private final DeployRepository deployRepository;

    @Override
    public Deploy initiateDeployment(DeployDTO dto) {
        Deploy deploy = Deploy.builder()
                .projectName(dto.getProjectName())
                .gitUrl(dto.getGitUrl())
                .subdomain(dto.getSubdomain())
                .status("BUILDING")
                .build();

        return deployRepository.save(deploy);
    }

    @Override
    public List<Deploy> getAllDeployments() {
        return deployRepository.findAll();
    }

    @Override
    public Deploy getDeploymentById(Long id) {
        return deployRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Deployment not found with ID: " + id));
    }

    @Override
    public void updateStatus(Long id, String status, String imageTag) {
        Deploy deploy = getDeploymentById(id);
        deploy.setStatus(status);
        if (imageTag != null) deploy.setImageTag(imageTag);
        deployRepository.save(deploy);
    }

    @Override
    public void deleteDeployment(Long id) {
        if (!deployRepository.existsById(id)) {
            throw new BusinessException("Cannot delete: Deployment not found with ID: " + id);
        }
        deployRepository.deleteById(id);
    }
}