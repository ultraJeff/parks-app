package com.redhat.smartcity;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class JwtGenerator {
    public String generateForUser(String username) {
        return Jwt
            .issuer("https://example.com/issuer")
            .upn(username)
            .groups(Set.of("Admin", "User"))
            .sign();
    };
}
