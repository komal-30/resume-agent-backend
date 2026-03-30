package com.resumeagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for CORS (Cross-Origin Resource Sharing).
 * 
 * Allows the React frontend (running on different port/domain) to make
 * API requests to this backend.
 * 
 * Development:
 * - Frontend: http://localhost:5173 (Vite dev server)
 * - Backend: http://localhost:8080 (Spring Boot)
 * 
 * Production:
 * - Frontend: https://yourapp.vercel.app
 * - Backend: https://yourapp.railway.app
 */
@Configuration
public class WebConfig {

    @Value("${cors.allowed.origins}")
    private String[] allowedOrigins;

    @Value("${cors.allowed.methods}")
    private String[] allowedMethods;

    @Value("${cors.allowed.headers}")
    private String allowedHeaders;

    @Value("${cors.allow.credentials}")
    private boolean allowCredentials;

    /**
     * Configure CORS mappings.
     * 
     * Allows:
     * - Specific origins (localhost:5173, Vercel domain)
     * - All HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
     * - All headers
     * - Credentials (cookies, authorization headers)
     * 
     * @return WebMvcConfigurer with CORS settings
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply to all endpoints
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods(allowedMethods)
                        .allowedHeaders(allowedHeaders)
                        .allowCredentials(allowCredentials)
                        .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}