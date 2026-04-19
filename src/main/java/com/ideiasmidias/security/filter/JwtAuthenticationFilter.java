package com.ideiasmidias.security.filter;

import com.ideiasmidias.security.model.AdminUserPrincipal;
import com.ideiasmidias.security.service.CustomAdminUserDetailsService;
import com.ideiasmidias.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomAdminUserDetailsService customAdminUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/api/admin/auth/login")
                || path.startsWith("/api/public/")
                || path.startsWith("/uploads/")
                || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            if (username == null || username.isBlank()) {
                log.warn(
                        "JWT rejected: missing username claim. path={}, method={}, remoteAddr={}",
                        request.getServletPath(),
                        request.getMethod(),
                        request.getRemoteAddr()
                );

                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customAdminUserDetailsService.loadUserByUsername(username);

                if (userDetails instanceof AdminUserPrincipal principal) {
                    if (!Boolean.TRUE.equals(principal.getIsActive())) {
                        log.warn(
                                "JWT rejected: inactive admin account. adminEmail={}, path={}, method={}, remoteAddr={}",
                                principal.getEmail(),
                                request.getServletPath(),
                                request.getMethod(),
                                request.getRemoteAddr()
                        );

                        SecurityContextHolder.clearContext();
                        filterChain.doFilter(request, response);
                        return;
                    }

                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.warn(
                                "JWT rejected: invalid or expired token. adminEmail={}, path={}, method={}, remoteAddr={}",
                                principal.getEmail(),
                                request.getServletPath(),
                                request.getMethod(),
                                request.getRemoteAddr()
                        );

                        SecurityContextHolder.clearContext();
                    }
                } else {
                    log.warn(
                            "JWT rejected: unexpected principal type. path={}, method={}, remoteAddr={}",
                            request.getServletPath(),
                            request.getMethod(),
                            request.getRemoteAddr()
                    );

                    SecurityContextHolder.clearContext();
                }
            }
        } catch (UsernameNotFoundException ex) {
            log.warn(
                    "JWT rejected: admin user not found. path={}, method={}, remoteAddr={}, reason={}",
                    request.getServletPath(),
                    request.getMethod(),
                    request.getRemoteAddr(),
                    ex.getMessage()
            );
            SecurityContextHolder.clearContext();
        } catch (Exception ex) {
            log.warn(
                    "JWT processing failed. path={}, method={}, remoteAddr={}, errorType={}, message={}",
                    request.getServletPath(),
                    request.getMethod(),
                    request.getRemoteAddr(),
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}