package com.example.ecommerce.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtProvider {

    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public String generateToken(Authentication auth) {

        log.info("Generating JWT token for user='{}'", auth.getName());

        // collect authorities as a list of role strings
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.debug("User roles extracted for token generation: {}", roles);

        String jwt = Jwts.builder()
                .setSubject(auth.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 846000000))
                .claim("email", auth.getName())
                .claim("roles", roles)
                .signWith(key)
                .compact();

        log.info("JWT token generated successfully for user='{}'", auth.getName());

        return jwt;
    }

    public String getEmailFromToken(String jwt) {

        log.debug("Extracting email from JWT token");

        jwt = jwt.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        String email = String.valueOf(claims.get("email"));

        log.debug("Email extracted successfully from token");

        return email;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String jwt) {

        log.debug("Extracting roles from JWT token");

        jwt = jwt.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        Object rolesObj = claims.get("roles");

        if (rolesObj instanceof List) {
            log.debug("Roles extracted successfully from token");
            return (List<String>) rolesObj;
        }

        log.warn("No roles found in JWT token");
        return List.of();
    }
}
