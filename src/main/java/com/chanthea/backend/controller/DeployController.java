package com.chanthea.backend.controller;

import com.chanthea.backend.dto.ApiResponse;
import com.chanthea.backend.dto.DeployDTO;
import com.chanthea.backend.model.Deploy;
import com.chanthea.backend.service.DeployService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
public class DeployController {

    private final DeployService deployService;

    @PostMapping
    public ResponseEntity<ApiResponse<Deploy>> createDeployment(@RequestBody DeployDTO deployDTO) {
        Deploy deploy = deployService.initiateDeployment(deployDTO);
        return ResponseEntity.ok(ApiResponse.success(deploy, "Deployment process initiated successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Deploy>>> getAllDeployments() {
        List<Deploy> deployments = deployService.getAllDeployments();
        return ResponseEntity.ok(ApiResponse.success(deployments, "Fetched all deployments"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Deploy>> getDeploymentById(@PathVariable Long id) {
        Deploy deploy = deployService.getDeploymentById(id);
        return ResponseEntity.ok(ApiResponse.success(deploy, "Deployment found"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String imageTag) {

        deployService.updateStatus(id, status, imageTag);
        return ResponseEntity.ok(ApiResponse.success(null, "Deployment status updated to " + status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeployment(@PathVariable Long id) {
        deployService.deleteDeployment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deployment deleted successfully"));
    }
}