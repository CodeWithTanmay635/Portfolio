package dev.tanmay.contactmanagementsystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tanmay.contactmanagementsystem.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // HTTP 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Tell client that response is JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Create our standard API response
        ApiResponse<Void> apiResponse =
                ApiResponse.error("Authentication required");

        // Convert Java object → JSON and send it
        objectMapper.writeValue(
                response.getWriter(),
                apiResponse
        );
    }
}