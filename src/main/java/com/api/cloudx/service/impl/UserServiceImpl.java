package com.api.cloudx.service.impl;

import com.api.cloudx.constant.AuthProvider;
import com.api.cloudx.dto.SignUpRequestDTO;
import com.api.cloudx.entities.UserEntities;
import com.api.cloudx.exception.BadRequestException;
import com.api.cloudx.exception.ResourceNotFoundException;
import com.api.cloudx.repository.UserRepository;
import com.api.cloudx.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntities findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public UserEntities registerUser(SignUpRequestDTO signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        UserEntities user = new UserEntities();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setProvider(AuthProvider.local);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        return userRepository.save(user);
    }
}
