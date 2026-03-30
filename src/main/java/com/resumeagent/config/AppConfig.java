package com.resumeagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Application configuration class.
 * 
 * Defines Spring beans for:
 * - RestTemplate (HTTP client for API calls)
 * - ObjectMapper (JSON serialization/deserialization)
 * - TextChunker (Resume text processing)
 * 
 * These beans are injected throughout the application via Spring's DI.
 */
@Configuration
public class AppConfig {

    @Value("${http.client.connection.timeout:30000}")
    private int connectionTimeout;

    @Value("${http.client.read.timeout:60000}")
    private int readTimeout;

    /**
     * Configure RestTemplate for making HTTP requests to external APIs.
     * 
     * Used by:
     * - ClaudeApiClient
     * - VoyageApiClient
     * - ElevenLabsApiClient
     * 
     * Configured with timeouts to prevent hanging requests.
     * 
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);

        RestTemplate restTemplate = new RestTemplate(factory);

        return restTemplate;
    }

    /**
     * Configure ObjectMapper for JSON serialization/deserialization.
     * 
     * Features:
     * - Java 8 date/time support (LocalDateTime, etc.)
     * - ISO-8601 date format (not timestamps)
     * - Pretty printing disabled for production
     * 
     * Used throughout the application for:
     * - Request/response body mapping
     * - Vector store JSON persistence
     * - API client request/response parsing
     * 
     * @return Configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register Java 8 date/time module
        mapper.registerModule(new JavaTimeModule());

        // Don't write dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ignore unknown properties when deserializing
        mapper.configure(
                com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);

        return mapper;
    }

    /**
     * Create TextChunker bean for resume text processing.
     * 
     * Reads chunk size configuration from application.properties.
     * If custom section patterns are provided, uses them instead of defaults.
     * 
     * @param minChunkSize   Minimum words per chunk
     * @param maxChunkSize   Maximum words per chunk
     * @param customPatterns Custom section header patterns (optional)
     * @return TextChunker instance
     */
    @Bean
    public com.resumeagent.utils.TextChunker textChunker(
            @Value("${rag.chunk.min.size:100}") int minChunkSize,
            @Value("${rag.chunk.max.size:800}") int maxChunkSize,
            @Value("${rag.section.patterns:}") String customPatterns) {

        if (customPatterns != null && !customPatterns.trim().isEmpty()) {
            // Use custom patterns from configuration
            return new com.resumeagent.utils.TextChunker(minChunkSize, maxChunkSize, customPatterns);
        } else {
            // Use default patterns
            return new com.resumeagent.utils.TextChunker(minChunkSize, maxChunkSize);
        }
    }
}