package com.api.cloudx.service;

import com.api.cloudx.dto.SignUpRequestDTO;
import com.api.cloudx.entities.UserEntities;

public interface UserService {
    UserEntities findById(Long id);
    UserEntities registerUser(SignUpRequestDTO signUpRequest);
}
