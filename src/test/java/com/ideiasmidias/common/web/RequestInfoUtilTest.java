package com.ideiasmidias.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestInfoUtilTest {

    @Test
    void prefersFirstAddressFromXForwardedFor() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.7, 70.41.3.18, 150.172.238.178");

        assertThat(RequestInfoUtil.getClientIp(request)).isEqualTo("203.0.113.7");
    }

    @Test
    void fallsBackToXRealIp() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("198.51.100.23");

        assertThat(RequestInfoUtil.getClientIp(request)).isEqualTo("198.51.100.23");
    }

    @Test
    void fallsBackToRemoteAddrWhenNoHeadersPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("10.0.0.5");

        assertThat(RequestInfoUtil.getClientIp(request)).isEqualTo("10.0.0.5");
    }

    @Test
    void readsUserAgentHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        assertThat(RequestInfoUtil.getUserAgent(request)).isEqualTo("Mozilla/5.0");
    }

    @Test
    void returnsNullForNullRequest() {
        assertThat(RequestInfoUtil.getClientIp(null)).isNull();
        assertThat(RequestInfoUtil.getUserAgent(null)).isNull();
    }
}
