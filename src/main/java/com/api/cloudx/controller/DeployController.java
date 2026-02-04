package com.api.cloudx.controller;

import com.api.cloudx.entities.DeployEntities;
import com.api.cloudx.service.DeployService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/deploys")
@RequiredArgsConstructor
public class DeployController {

    private final DeployService deployService;

    @PostMapping
    public ResponseEntity<DeployEntities> createDeployment(@RequestBody DeployEntities request) {
        return ResponseEntity.ok(deployService.startDeployment(request));
    }

    @GetMapping
    public ResponseEntity<List<DeployEntities>> getAll() {
        return ResponseEntity.ok(deployService.getAllDeployments());
    }

    @PatchMapping("/{id}/callback")
    public ResponseEntity<?> callback(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        String status = updates.get("status");
        String imageTag = updates.get("imageTag");

        deployService.updateStatus(id, status, imageTag);
        return ResponseEntity.ok().build();
    }
}