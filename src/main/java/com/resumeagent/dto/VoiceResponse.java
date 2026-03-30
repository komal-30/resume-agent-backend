package com.resumeagent.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for voice-based answers.
 * 
 * Contains both the text answer and the audio (base64-encoded MP3).
 * 
 * Returned from: POST /api/voice
 * 
 * Example response:
 * {
 * "audioBase64": "SUQzBAAAAAAAI1RTU0UAAAA...",
 * "text": "Koko has expertise in Java, Python, and React...",
 * "timestamp": "2024-03-24T10:30:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceResponse {

    /**
     * Base64-encoded audio data (MP3 format from ElevenLabs)
     * Frontend decodes this and plays it through the AudioPlayer component
     */
    @JsonProperty("audioBase64")
    private String audioBase64;

    /**
     * The text version of the answer
     * Displayed in the UI while audio plays
     */
    @JsonProperty("text")
    private String text;

    /**
     * Timestamp when the response was generated
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    /**
     * Convenience constructor for quick response creation
     */
    public VoiceResponse(String audioBase64, String text) {
        this.audioBase64 = audioBase64;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }
}