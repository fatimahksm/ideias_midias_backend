package com.ideiasmidias.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideiasmidias.common.response.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // The app's own bean, not `new ObjectMapper()`: that default mapper has
    // no JSR-310 module, so serializing this response's LocalDateTime
    // timestamp used to blow up on every 401 — and since that happened
    // mid-write, the response was left half-committed, which is what caused
    // the cascading "response already closed" noise seen after it.
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .success(false)
                .message("Authentication is required to access this resource")
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}