package com.api.cloudx.controller;

import com.api.cloudx.entities.UserEntities;
import com.api.cloudx.exception.ResourceNotFoundException;
import com.api.cloudx.repository.UserRepository;
import com.api.cloudx.security.CurrentUser;
import com.api.cloudx.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasAuthority('USER') or hasRole('USER')")
    public UserEntities getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
