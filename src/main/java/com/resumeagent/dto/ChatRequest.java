package com.resumeagent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for text-based chat questions.
 * 
 * Endpoint: POST /api/chat
 * 
 * Example request body:
 * {
 * "question": "Tell me about Koko's work experience"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    /**
     * The user's question about the resume
     * Must be non-empty and between 1-500 characters
     */
    @JsonProperty("question")
    @NotBlank(message = "Question cannot be empty")
    @Size(min = 1, max = 500, message = "Question must be between 1 and 500 characters")
    private String question;
}