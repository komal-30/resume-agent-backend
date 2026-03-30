package com.resumeagent.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single result from a vector similarity search.
 * 
 * Contains:
 * - The matched text chunk
 * - Similarity score (0.0 to 1.0, higher is better)
 * - Optional metadata from the original embedding
 * 
 * Used in RAG pipeline to rank and select relevant context.
 * 
 * Example:
 * SearchResult(
 * text = "Koko worked at TechCorp as Senior Developer...",
 * similarity = 0.876,
 * metadata = {"section": "Work Experience"}
 * )
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    /**
     * The text content of the matched chunk
     */
    private String text;

    /**
     * Cosine similarity score between query and this chunk
     * Range: 0.0 (completely different) to 1.0 (identical)
     * Typical threshold: 0.7 for relevant matches
     */
    private double similarity;

    /**
     * Metadata from the original embedding
     * Can include section name, source, etc.
     */
    private Map<String, Object> metadata;

    /**
     * Constructor without metadata
     */
    public SearchResult(String text, double similarity) {
        this.text = text;
        this.similarity = similarity;
        this.metadata = null;
    }

    /**
     * Check if this result is above the relevance threshold
     */
    public boolean isRelevant(double threshold) {
        return this.similarity >= threshold;
    }

    /**
     * Get a formatted string representation
     */
    public String getFormattedResult() {
        return String.format("Score: %.3f | %s", similarity, text);
    }
}
