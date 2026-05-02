package com.ideiasmidias.common.web;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestInfoUtil {

    private RequestInfoUtil() {}

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return null;

        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) return null;
        return request.getHeader("User-Agent");
    }
}