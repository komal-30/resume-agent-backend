package com.resumeagent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for voice-based questions.
 * 
 * Note: The frontend uses Web Speech API to convert speech to text,
 * so this endpoint receives the transcribed text (not audio).
 * 
 * Endpoint: POST /api/voice
 * 
 * Example request body:
 * {
 * "question": "What are Koko's technical skills?"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceRequest {

    /**
     * The transcribed question from the user's voice input
     * Must be non-empty and between 1-500 characters
     */
    @JsonProperty("question")
    @NotBlank(message = "Question cannot be empty")
    @Size(min = 1, max = 500, message = "Question must be between 1 and 500 characters")
    private String question;
}