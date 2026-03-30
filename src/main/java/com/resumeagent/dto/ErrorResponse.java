package com.resumeagent.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response DTO for all API errors.
 * 
 * Provides consistent error format across the application.
 * 
 * Example error response:
 * {
 * "error": "ApiException",
 * "message": "Failed to connect to Claude API",
 * "timestamp": "2024-03-24T10:30:00",
 * "path": "/api/chat"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Error type or exception class name
     */
    @JsonProperty("error")
    private String error;

    /**
     * Human-readable error message
     */
    @JsonProperty("message")
    private String message;

    /**
     * Timestamp when the error occurred
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    /**
     * Request path where the error occurred
     */
    @JsonProperty("path")
    private String path;

    /**
     * Convenience constructor without path
     */
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = null;
    }

    /**
     * Convenience constructor with all required fields
     */
    public ErrorResponse(String error, String message, String path) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}