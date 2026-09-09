package com.ideiasmidias.common.exception;

import com.ideiasmidias.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * The container-level multipart ceiling — the one that actually aborts an
     * oversized upload before any controller runs. Read here only so the error
     * message can quote the real limit instead of a duplicated constant.
     */
    @Value("${spring.servlet.multipart.max-file-size:200MB}")
    private DataSize maxUploadSize;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource not found. path={}, message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        log.warn("Bad request. path={}, message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), null, request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        log.warn("Conflict. path={}, message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), null, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        log.warn("Unauthorized access. path={}, message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), null, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request
    ) {
        log.warn("Forbidden. path={}, message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage(), null, request);
    }

    /**
     * Raised by {@code @PreAuthorize} when an authenticated admin lacks the
     * required role — an ADMIN calling a SUPER_ADMIN-only endpoint, typically.
     * Without this it falls through to the generic handler and surfaces as a
     * 500, which the frontend cannot tell apart from a real server fault.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Access denied. path={}, method={}, message={}",
                request.getRequestURI(),
                request.getMethod(),
                ex.getMessage()
        );

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "You do not have permission to perform this action.",
                null,
                request
        );
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiErrorResponse> handleTooManyRequests(
            TooManyRequestsException ex,
            HttpServletRequest request
    ) {
        log.warn("Too many requests. path={}, message={}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", ex.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : error.getObjectName();

            errors.put(fieldName, error.getDefaultMessage());
        });

        log.warn("Validation failed. path={}, errors={}", request.getRequestURI(), errors);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Validation failed",
                errors,
                request
        );
    }

    /**
     * A body Jackson could not read at all: malformed JSON, or a value that is
     * not one of an enum's constants. That is a caller mistake, not a server
     * fault. The parser's own message names internal class paths, so it stays
     * in the log and the client gets a stable, generic sentence instead.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Unreadable request body. path={}, method={}, message={}",
                request.getRequestURI(),
                request.getMethod(),
                ex.getMostSpecificCause().getMessage()
        );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST_BODY",
                "The request body is malformed or contains a value this endpoint does not accept.",
                null,
                request
        );
    }

    /**
     * A path variable or query parameter of the wrong type — {@code /items/abc}
     * where an id is expected, or an unknown enum name in a filter.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(ex.getName(), "Value is not valid for this parameter");

        log.warn(
                "Parameter type mismatch. path={}, parameter={}, value={}",
                request.getRequestURI(),
                ex.getName(),
                ex.getValue()
        );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "One of the request parameters is not valid.",
                errors,
                request
        );
    }

    /**
     * No handler matched the URL. Spring raises this instead of returning 404
     * on its own once a controller advice is in play, so it has to be mapped
     * back explicitly.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        if (isMediaRequest(request)) {
            log.warn("Media file not found. path={}", request.getRequestURI());
            return ResponseEntity.notFound().build();
        }

        log.warn(
                "No handler for request. path={}, method={}",
                request.getRequestURI(),
                request.getMethod()
        );

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "ENDPOINT_NOT_FOUND",
                "The requested endpoint does not exist.",
                null,
                request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Method not allowed. path={}, method={}, supported={}",
                request.getRequestURI(),
                request.getMethod(),
                ex.getSupportedHttpMethods()
        );

        return buildErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                "This endpoint does not support " + request.getMethod() + " requests.",
                null,
                request
        );
    }

    /**
     * Tomcat aborts an oversized upload before the controller — and before
     * {@code MediaFileValidator} ever sees the file — so the friendly
     * "too large" message has to be produced here as well.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Upload rejected: larger than the configured limit. path={}, limit={}MB",
                request.getRequestURI(),
                maxUploadSize.toMegabytes()
        );

        return buildErrorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "PAYLOAD_TOO_LARGE",
                "This file is too large. The maximum upload size is "
                        + maxUploadSize.toMegabytes() + " MB.",
                null,
                request
        );
    }

    @ExceptionHandler(ClientAbortException.class)
    public ResponseEntity<Void> handleClientAbort(
            ClientAbortException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Client aborted connection. path={}, errorType={}, message={}",
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIoException(
            IOException ex,
            HttpServletRequest request
    ) {
        if (isMediaRequest(request)) {
            log.warn(
                    "I/O error during media request. path={}, errorType={}, message={}",
                    request.getRequestURI(),
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
            return ResponseEntity.noContent().build();
        }

        log.error(
                "I/O exception. path={}, errorType={}, message={}",
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
        );

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "IO_ERROR",
                "An I/O error occurred",
                null,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        if (isMediaRequest(request)) {
            log.warn(
                    "Unhandled exception during media request. path={}, errorType={}, message={}",
                    request.getRequestURI(),
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
            return ResponseEntity.noContent().build();
        }

        log.error(
                "Unhandled exception. path={}, errorType={}, message={}",
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
        );

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                null,
                request
        );
    }

    private boolean isMediaRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && path.startsWith("/uploads/");
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String code,
            String message,
            Map<String, String> errors,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .status(status.value())
                .code(code)
                .path(request.getRequestURI())
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}