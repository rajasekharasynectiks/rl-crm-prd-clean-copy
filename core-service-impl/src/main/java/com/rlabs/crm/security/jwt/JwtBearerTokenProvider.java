package com.rlabs.crm.security.jwt;

import com.rlabs.crm.service.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;

import static com.rlabs.crm.security.SecurityUtils.AUTHORITIES_KEY;
import static com.rlabs.crm.security.SecurityUtils.JWT_ALGORITHM;

@Component
public class JwtBearerTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(JwtBearerTokenProvider.class);

    private static final String BEARER = "Bearer ";

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds}")
    private int jwtExpirationSec;

    @Autowired
    private JwtEncoder jwtEncoder;

    public String createValidTokenForUser(UserDetailsImpl userDetails) {
        var now = Instant.now();
//        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
//            .collect(Collectors.toList());

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtExpirationSec))
            .subject(userDetails.getUsername())
            .claims(customClaim -> customClaim.put(AUTHORITIES_KEY, Collections.singletonList(userDetails.getAuthorities())))
            .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

}
