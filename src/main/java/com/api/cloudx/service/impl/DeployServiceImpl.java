package com.api.cloudx.service.impl;

import com.api.cloudx.entities.DeployEntities;
import com.api.cloudx.repository.DeployRepository;
import com.api.cloudx.service.DeployService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeployServiceImpl implements DeployService {

    private final DeployRepository repository;
    private final RestTemplate restTemplate;

    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.user}")
    private String jenkinsUser;

    @Value("${jenkins.token}")
    private String jenkinsToken;

    @Override
    public List<DeployEntities> getAllDeployments() {
        return repository.findAll();
    }

    @Override
    public DeployEntities getDeploymentById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("រកមិនឃើញទិន្នន័យឡើយ!"));
    }

    @Override
    public DeployEntities startDeployment(DeployEntities request) {
        // ១. រក្សាទុកក្នុង DB ជាមួយ status BUILDING
        request.setStatus("BUILDING");
        DeployEntities savedDeployEntities = repository.save(request);

        // ២. ហៅទៅ Jenkins Pipeline
        try {
            triggerJenkins(savedDeployEntities);
        } catch (Exception e) {
            savedDeployEntities.setStatus("FAILED");
            repository.save(savedDeployEntities);
            throw new RuntimeException("Jenkins Trigger Error: " + e.getMessage());
        }

        return savedDeployEntities;
    }

    private void triggerJenkins(DeployEntities deployEntities) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jenkinsUser, jenkinsToken);

        // ប្រើវិធីនេះជំនួសវិញ ប្រសិនបើ fromHttpUrl រកមិនឃើញ
        String url = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("your-jenkins-ip")
                .port(8080)
                .path("/job/deployEntities-pipeline/buildWithParameters")
                .queryParam("DEPLOY_ID", deployEntities.getId())
                .queryParam("PROJECT_NAME", deployEntities.getProjectName())
                .queryParam("GIT_URL", deployEntities.getGitUrl())
                .queryParam("IMAGE_TAG", (deployEntities.getImageTag() != null) ? deployEntities.getImageTag() : "latest")
                .build()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    @Override
    public DeployEntities updateStatus(Long id, String status, String imageTag) {
        DeployEntities deployEntities = getDeploymentById(id);
        deployEntities.setStatus(status);

        if (imageTag != null) {
            deployEntities.setImageTag(imageTag);
        }

        return repository.save(deployEntities);
    }
}