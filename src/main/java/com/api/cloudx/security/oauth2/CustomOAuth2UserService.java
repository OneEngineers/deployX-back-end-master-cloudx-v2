package com.api.cloudx.security.oauth2;

import com.api.cloudx.constant.AuthProvider;
import com.api.cloudx.entities.UserEntities;
import com.api.cloudx.repository.UserRepository;
import com.api.cloudx.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );

        String email = oAuth2UserInfo.getEmail();
        if (StringUtils.isEmpty(email)) {
            email = oAuth2UserInfo.getId() + "@" + oAuth2UserRequest.getClientRegistration().getRegistrationId() + ".com";
        }

        Optional<UserEntities> userOptional = userRepository.findByEmail(email);
        UserEntities user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2UserInfo, oAuth2UserRequest);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo, email);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private UserEntities registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo, String email) {
        UserEntities user = new UserEntities();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setEmail(email);
        user.setImageUrl(oAuth2UserInfo.getImageUrl());

        // FALLBACK LOGIC: If name is null, use part of the email
        String name = oAuth2UserInfo.getName();
        if (name == null || name.trim().isEmpty()) {
            name = email.split("@")[0];
        }
        // Check if name exists, if so, append ID to make it unique
        if (userRepository.existsByName(name)) {
            name = name + "_" + oAuth2UserInfo.getId();
        }
        user.setName(name);

        user.setProviderToken(oAuth2UserRequest.getAccessToken().getTokenValue());
        return userRepository.save(user);
    }

    private UserEntities updateExistingUser(UserEntities existingUser, OAuth2UserInfo oAuth2UserInfo, OAuth2UserRequest oAuth2UserRequest) {
        String name = oAuth2UserInfo.getName();

        // If GitHub doesn't provide a name, don't overwrite the existing name with null!
        if (StringUtils.hasText(name)) {
            existingUser.setName(name);
        }
        // If the existing name is somehow already null, use email as fallback
        else if (!StringUtils.hasText(existingUser.getName())) {
            existingUser.setName(existingUser.getEmail().split("@")[0]);
        }

        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        existingUser.setProviderToken(oAuth2UserRequest.getAccessToken().getTokenValue());

        return userRepository.save(existingUser);
    }
}
