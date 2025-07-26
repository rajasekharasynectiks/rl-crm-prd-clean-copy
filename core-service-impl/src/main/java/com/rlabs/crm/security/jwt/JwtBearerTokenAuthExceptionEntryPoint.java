package com.rlabs.crm.security.jwt;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtBearerTokenAuthExceptionEntryPoint implements AuthenticationEntryPoint {

    private String realmName;

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized bearer token. Error message: {}", authException.getMessage());

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, Object> parameters = new LinkedHashMap();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }

        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
            log.error("Bearer token oauth2 exception. Error code: {}", error.getErrorCode());
            parameters.put("error", error.getErrorCode());
            if (StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }

            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }

            if (error instanceof BearerTokenError) {
                BearerTokenError bearerTokenError = (BearerTokenError) error;
                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }

                status = ((BearerTokenError) error).getHttpStatus();
                parameters.put("status", status);
            }
        } else {
            log.error("Bearer token authentication exception. Exception type: {}", authException.getClass().getName());
            parameters.put("title", status);
            parameters.put("status", status.value());
            parameters.put("detail", "JWT token either invalid or expired");
            parameters.put("instance", request.getRequestURI());
            parameters.put("message", "error."+"http."+status.value());
            parameters.put("path", request.getRequestURI());

        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE,computeWWWAuthenticateHeaderValue(parameters));
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), parameters);
    }

    private static String computeWWWAuthenticateHeaderValue(Map<String, Object> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for(String s: parameters.keySet()){
                wwwAuthenticate.append(s).append("=\"").append(parameters.get(s)).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }
        return wwwAuthenticate.toString();
    }
}
