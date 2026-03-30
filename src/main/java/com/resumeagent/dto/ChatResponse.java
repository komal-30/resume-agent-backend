package com.resumeagent.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for text-based chat answers.
 * 
 * Returned from: POST /api/chat
 * 
 * Example response:
 * {
 * "answer": "Koko has 5 years of software engineering experience...",
 * "timestamp": "2024-03-24T10:30:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    /**
     * The AI-generated answer based on resume context
     */
    @JsonProperty("answer")
    private String answer;

    /**
     * Timestamp when the response was generated
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    /**
     * Convenience constructor for quick response creation
     */
    public ChatResponse(String answer) {
        this.answer = answer;
        this.timestamp = LocalDateTime.now();
    }
}