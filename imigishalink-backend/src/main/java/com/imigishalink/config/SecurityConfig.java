package com.imigishalink.config;

import com.imigishalink.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/health",
                    "/api/v1/auth/**",
                    "/api/auth/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api/v1/public/**",
                    "/api/v1/categories/**"
                ).permitAll()
                // Allow public access to general NGO endpoints (must come before authenticated rules)
                .requestMatchers(HttpMethod.GET, "/api/v1/ngos").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ngos/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ngos/top").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/ngos/search").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/ngos/register").permitAll()
                // Require authentication for my-ngos endpoint (requires NGO role via @PreAuthorize)
                .requestMatchers(HttpMethod.GET, "/api/v1/ngos/my-ngos").authenticated()
                // Allow public access to GET donations, but require auth for specific endpoints
                // More specific rules first - authenticated endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/donations/pending").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/donations/my-donations").authenticated()
                // Public access to general donation endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/donations").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/donations/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/donations/urgent").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/donations/search").permitAll()
                // Admin endpoints - require authentication (role checked via @PreAuthorize)
                .requestMatchers(HttpMethod.GET, "/api/v1/users").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/stats").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/locations/provinces").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/locations/districts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/locations/sectors").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/locations/cells").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/locations/villages").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/locations/hierarchy").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/search/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/messages/contact").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000", 
            "http://localhost:3001",
            "http://localhost:5173"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}