package com.medicinaviva.consultation.config;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import com.medicinaviva.consultation.model.CustomJwtAuthToken;
import com.medicinaviva.consultation.model.exception.UnauthorizedException;

public class JWTConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Instant expiration = jwt.getExpiresAt();
        if (expiration != null && expiration.isBefore(Instant.now()))
            throw new UnauthorizedException("Unathorized.");

        Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
        Collection<String> roles = realmAccess.get("roles");
        var grants = roles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

        String userIdentifier = jwt.getClaim("user_identifier");
        return new CustomJwtAuthToken(jwt, grants, userIdentifier);
    }

}
