package com.resumeagent.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a text chunk with its vector embedding and metadata.
 * 
 * This model is used for:
 * - Storing resume sections with their embeddings
 * - Performing similarity searches
 * - Persisting to JSON file
 * 
 * Example:
 * {
 * "id": "chunk_001",
 * "text": "Koko has 5 years of experience in software engineering...",
 * "embedding": [0.234, -0.456, 0.789, ...],
 * "metadata": {
 * "section": "Work Experience",
 * "chunkIndex": 0
 * }
 * }
 */
@Data
@NoArgsConstructor
// @AllArgsConstructor

public class EmbeddingData {

    /**
     * Unique identifier for this embedding
     */
    @JsonProperty("id")
    private String id;

    /**
     * Original text content that was embedded
     */
    @JsonProperty("text")
    private String text;

    /**
     * Vector embedding representation (typically 1024 or 1536 dimensions)
     */
    @JsonProperty("embedding")
    private double[] embedding;

    /**
     * Additional metadata about this chunk
     * Can include: section name, page number, chunk index, etc.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Constructor without metadata
     */
    public EmbeddingData(String id, String text, double[] embedding) {
        this.id = id;
        this.text = text;
        this.embedding = embedding;
        this.metadata = new HashMap<>();
    }

    /**
     * JSON constructor for deserialization
     */
    @JsonCreator
    public EmbeddingData(
            @JsonProperty("id") String id,
            @JsonProperty("text") String text,
            @JsonProperty("embedding") double[] embedding,
            @JsonProperty("metadata") Map<String, Object> metadata) {
        this.id = id;
        this.text = text;
        this.embedding = embedding;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * Add metadata entry
     */
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }

    /**
     * Get metadata value
     */
    public Object getMetadata(String key) {
        return this.metadata != null ? this.metadata.get(key) : null;
    }
}