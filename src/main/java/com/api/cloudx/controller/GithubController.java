package com.api.cloudx.controller;

import com.api.cloudx.entities.UserEntities;
import com.api.cloudx.security.CurrentUser;
import com.api.cloudx.security.UserPrincipal;
import com.api.cloudx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

@RestController
public class GithubController {
    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/user/github/repos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getGithubRepos(@CurrentUser UserPrincipal userPrincipal) {

        UserEntities user = userService.findById(userPrincipal.getId());

        if (user.getProviderToken() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No GitHub token found. Please re-login.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getProviderToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    "https://api.github.com/user/repos?sort=updated",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("GitHub API Error: " + e.getMessage());
        }
    }
}
