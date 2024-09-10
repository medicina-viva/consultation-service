package com.medicinaviva.consultation.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CustomJwtAuthToken extends  JwtAuthenticationToken {
      private final String userIdentifier;

    public CustomJwtAuthToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String userIdentifier) {
        super(jwt, authorities);
        this.userIdentifier = userIdentifier;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }
}
