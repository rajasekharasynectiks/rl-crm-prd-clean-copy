package com.rlabs.crm.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtBearerTokenAccessDeniedExceptionEntryPoint implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(JwtBearerTokenAccessDeniedExceptionEntryPoint.class);
    private String realmName;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        logger.error("Bearer token access denied exception. Error message: {}", accessDeniedException.getMessage());

        Map<String, Object> parameters = new LinkedHashMap();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }
        logger.error("OAuth2 bearer token access denied exception");

        parameters.put("title", HttpStatus.FORBIDDEN);
        parameters.put("status", HttpStatus.FORBIDDEN.value());
        parameters.put("detail", "OAuth2 bearer token access denied exception");
        parameters.put("instance", request.getRequestURI());
        parameters.put("message", "error."+"http."+HttpStatus.FORBIDDEN.value());
        parameters.put("path", request.getRequestURI());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), parameters);
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    private static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;

            for(Iterator var3 = parameters.entrySet().iterator(); var3.hasNext(); ++i) {
                Map.Entry<String, String> entry = (Map.Entry)var3.next();
                wwwAuthenticate.append((String)entry.getKey()).append("=\"").append((String)entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
            }
        }

        return wwwAuthenticate.toString();
    }
}
