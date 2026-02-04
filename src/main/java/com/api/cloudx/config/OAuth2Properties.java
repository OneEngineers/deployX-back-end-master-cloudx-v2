package com.api.cloudx.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class OAuth2Properties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();

    @Setter
    @Getter
    public static class Auth {
        private String tokenSecret;
        private long tokenExpiration;
    }

    @Getter
    public static final class OAuth2 {
        private final List<String> authorizedRedirectUris = new ArrayList<>();
    }
}
