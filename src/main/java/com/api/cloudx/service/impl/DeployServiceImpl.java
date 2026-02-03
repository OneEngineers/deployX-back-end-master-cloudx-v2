package com.api.cloudx.service.impl;

import com.api.cloudx.model.Deploy;
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
    public List<Deploy> getAllDeployments() {
        return repository.findAll();
    }

    @Override
    public Deploy getDeploymentById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("រកមិនឃើញទិន្នន័យឡើយ!"));
    }

    @Override
    public Deploy startDeployment(Deploy request) {
        // ១. រក្សាទុកក្នុង DB ជាមួយ status BUILDING
        request.setStatus("BUILDING");
        Deploy savedDeploy = repository.save(request);

        // ២. ហៅទៅ Jenkins Pipeline
        try {
            triggerJenkins(savedDeploy);
        } catch (Exception e) {
            savedDeploy.setStatus("FAILED");
            repository.save(savedDeploy);
            throw new RuntimeException("Jenkins Trigger Error: " + e.getMessage());
        }

        return savedDeploy;
    }

    private void triggerJenkins(Deploy deploy) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jenkinsUser, jenkinsToken);

        // ប្រើវិធីនេះជំនួសវិញ ប្រសិនបើ fromHttpUrl រកមិនឃើញ
        String url = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("your-jenkins-ip")
                .port(8080)
                .path("/job/deploy-pipeline/buildWithParameters")
                .queryParam("DEPLOY_ID", deploy.getId())
                .queryParam("PROJECT_NAME", deploy.getProjectName())
                .queryParam("GIT_URL", deploy.getGitUrl())
                .queryParam("IMAGE_TAG", (deploy.getImageTag() != null) ? deploy.getImageTag() : "latest")
                .build()
                .toUriString();

        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    @Override
    public Deploy updateStatus(Long id, String status, String imageTag) {
        Deploy deploy = getDeploymentById(id);
        deploy.setStatus(status);

        if (imageTag != null) {
            deploy.setImageTag(imageTag);
        }

        return repository.save(deploy);
    }
}