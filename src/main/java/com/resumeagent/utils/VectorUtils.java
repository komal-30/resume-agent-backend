package com.resumeagent.utils;

/**
 * Utility class for vector operations used in similarity search.
 * 
 * Provides:
 * - Cosine similarity calculation
 * - Vector normalization
 * - Dot product computation
 * 
 * These operations are core to the RAG pipeline for finding
 * semantically similar resume chunks based on query embeddings.
 */
public class VectorUtils {

    /**
     * Calculate cosine similarity between two vectors.
     * 
     * Cosine similarity measures the cosine of the angle between two vectors.
     * Result ranges from -1 (opposite) to 1 (identical).
     * For normalized embeddings, this equals the dot product.
     * 
     * Formula: cosine_similarity(A, B) = (A · B) / (||A|| * ||B||)
     * 
     * @param vector1 First embedding vector
     * @param vector2 Second embedding vector
     * @return Similarity score between 0.0 and 1.0 (higher = more similar)
     * @throws IllegalArgumentException if vectors have different dimensions
     */
    public static double cosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1 == null || vector2 == null) {
            throw new IllegalArgumentException("Vectors cannot be null");
        }

        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException(
                    String.format("Vector dimensions don't match: %d vs %d",
                            vector1.length, vector2.length));
        }

        if (vector1.length == 0) {
            throw new IllegalArgumentException("Vectors cannot be empty");
        }

        double dotProduct = dotProduct(vector1, vector2);
        double magnitude1 = magnitude(vector1);
        double magnitude2 = magnitude(vector2);

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }

    /**
     * Calculate the dot product of two vectors.
     * 
     * Dot product: A · B = Σ(a_i * b_i)
     * 
     * @param vector1 First vector
     * @param vector2 Second vector
     * @return Dot product value
     */
    public static double dotProduct(double[] vector1, double[] vector2) {
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            sum += vector1[i] * vector2[i];
        }
        return sum;
    }

    /**
     * Calculate the magnitude (L2 norm) of a vector.
     * 
     * Magnitude: ||A|| = √(Σ(a_i²))
     * 
     * @param vector Input vector
     * @return Magnitude of the vector
     */
    public static double magnitude(double[] vector) {
        double sum = 0.0;
        for (double value : vector) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }

    /**
     * Normalize a vector to unit length.
     * 
     * Normalized vector: A' = A / ||A||
     * 
     * @param vector Input vector
     * @return Normalized vector (unit length)
     */
    public static double[] normalize(double[] vector) {
        double mag = magnitude(vector);

        if (mag == 0.0) {
            return vector.clone();
        }

        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / mag;
        }

        return normalized;
    }

    /**
     * Check if two vectors are approximately equal (within epsilon).
     * 
     * @param vector1 First vector
     * @param vector2 Second vector
     * @param epsilon Tolerance threshold
     * @return true if vectors are approximately equal
     */
    public static boolean areApproximatelyEqual(double[] vector1, double[] vector2, double epsilon) {
        if (vector1.length != vector2.length) {
            return false;
        }

        for (int i = 0; i < vector1.length; i++) {
            if (Math.abs(vector1[i] - vector2[i]) > epsilon) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the dimension of a vector.
     * 
     * @param vector Input vector
     * @return Dimension count
     */
    public static int getDimension(double[] vector) {
        return vector != null ? vector.length : 0;
    }
}