package com.imigishalink.security;

import com.imigishalink.config.JwtService;
import com.imigishalink.users.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserService userService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String requestPath = request.getRequestURI();
        
        // Skip JWT processing for public endpoints even if token is present
        if (isPublicEndpoint(requestPath, request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Token is invalid, but don't block the request - let SecurityConfig handle it
                    log.debug("Invalid JWT token for user: {}", userEmail);
                }
            }
        } catch (Exception e) {
            // If token processing fails, log but don't block - let SecurityConfig handle authorization
            log.debug("Error processing JWT token: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String path, String method) {
        // Public endpoints that should skip JWT validation
        if (path == null) return false;
        
        // Auth endpoints
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/api/auth/")) {
            return true;
        }
        
        // Public GET endpoints
        if ("GET".equalsIgnoreCase(method)) {
            if (path.startsWith("/api/v1/ngos") && !path.contains("/my-ngos")) {
                return true;
            }
            if (path.startsWith("/api/v1/donations") && 
                !path.contains("/pending") && 
                !path.contains("/my-donations")) {
                return true;
            }
            if (path.startsWith("/api/v1/categories/")) {
                return true;
            }
            if (path.startsWith("/api/v1/locations/")) {
                return true;
            }
            if (path.startsWith("/api/v1/search/")) {
                return true;
            }
        }
        
        // Other public endpoints
        if (path.equals("/") || path.equals("/health") || 
            path.startsWith("/v3/api-docs/") || 
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/api/v1/public/") ||
            path.startsWith("/api/v1/messages/contact")) {
            return true;
        }
        
        return false;
    }
}