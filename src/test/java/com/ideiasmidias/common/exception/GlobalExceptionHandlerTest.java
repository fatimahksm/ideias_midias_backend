package com.ideiasmidias.common.exception;

import com.ideiasmidias.common.response.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Every case here used to leave the API through the catch-all handler as a 500,
 * which the frontend could not tell apart from a genuine server fault. These
 * assert the status codes stay mapped.
 */
class GlobalExceptionHandlerTest {

    private static final long MAX_UPLOAD_MB = 200;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        ReflectionTestUtils.setField(handler, "maxUploadSize", DataSize.ofMegabytes(MAX_UPLOAD_MB));
    }

    private MockHttpServletRequest request(String method, String uri) {
        return new MockHttpServletRequest(method, uri);
    }

    @Test
    @DisplayName("an ADMIN calling a SUPER_ADMIN endpoint gets 403, not 500")
    void authorizationDeniedIsForbidden() {
        ResponseEntity<ApiErrorResponse> response = handler.handleAuthorizationDenied(
                new AuthorizationDeniedException("Access Denied"),
                request("DELETE", "/api/admin/sections/1")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("FORBIDDEN");
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    @DisplayName("malformed JSON or an unknown enum value gets 400, not 500")
    void unreadableBodyIsBadRequest() {
        ResponseEntity<ApiErrorResponse> response = handler.handleUnreadableBody(
                new HttpMessageNotReadableException(
                        "JSON parse error: not one of the accepted values",
                        new MockHttpInputMessage(new byte[0])
                ),
                request("POST", "/api/admin/sections")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("MALFORMED_REQUEST_BODY");
        // The parser's own message names internal classes; it must not leak out.
        assertThat(response.getBody().getMessage()).doesNotContain("JSON parse error");
    }

    @Test
    @DisplayName("an unknown endpoint gets 404, not 500")
    void unknownEndpointIsNotFound() {
        ResponseEntity<?> response = handler.handleNoResourceFound(
                new NoResourceFoundException(HttpMethod.GET, "/api/admin", "/api/admin/does-not-exist"),
                request("GET", "/api/admin/does-not-exist")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ApiErrorResponse.class);
        assertThat(((ApiErrorResponse) response.getBody()).getCode()).isEqualTo("ENDPOINT_NOT_FOUND");
    }

    @Test
    @DisplayName("a missing media file returns a bare 404, never an error envelope")
    void missingMediaFileIsBareNotFound() {
        ResponseEntity<?> response = handler.handleNoResourceFound(
                new NoResourceFoundException(HttpMethod.GET, "/uploads", "/uploads/media/gone.png"),
                request("GET", "/uploads/media/gone.png")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("the wrong HTTP method gets 405, not 500")
    void wrongMethodIsMethodNotAllowed() {
        ResponseEntity<ApiErrorResponse> response = handler.handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("POST", List.of("GET")),
                request("POST", "/api/admin/media-library")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("METHOD_NOT_ALLOWED");
    }

    @Test
    @DisplayName("an oversized upload gets 413 and the message quotes the real limit")
    void oversizedUploadIsPayloadTooLarge() {
        ResponseEntity<ApiErrorResponse> response = handler.handleMaxUploadSizeExceeded(
                new MaxUploadSizeExceededException(DataSize.ofMegabytes(MAX_UPLOAD_MB).toBytes()),
                request("POST", "/api/admin/media-library/upload")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("PAYLOAD_TOO_LARGE");
        assertThat(response.getBody().getMessage()).contains(String.valueOf(MAX_UPLOAD_MB));
    }
}
