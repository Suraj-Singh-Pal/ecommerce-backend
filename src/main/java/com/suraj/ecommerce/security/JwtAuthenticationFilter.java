package com.suraj.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        String token = null;
        String username = null;

        if (authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")) {

            token = authorizationHeader.substring(7);

            try {
                username = jwtService.extractUsername(token);
            } catch (Exception exception) {
                // Invalid token को ignore किया जाएगा।
                // आगे Spring Security authorization handle करेगा।
            }
        }

        if (username != null
                && SecurityContextHolder.getContext()
                .getAuthentication() == null) {

            try {
                String role = jwtService.extractRole(token);

                if (role != null && !role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }

                List<SimpleGrantedAuthority> authorities =
                        role == null
                                ? List.of()
                                : List.of(new SimpleGrantedAuthority(role));

                if (jwtService.isTokenValid(token, username)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }

            } catch (Exception exception) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}