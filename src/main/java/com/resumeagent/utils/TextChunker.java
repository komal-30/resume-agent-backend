package com.resumeagent.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for chunking resume text into semantic sections.
 * 
 * Auto-detects resume sections based on common patterns:
 * - Summary/Objective
 * - Work Experience / Professional Experience
 * - Education
 * - Skills / Technical Skills
 * - Projects
 * - Certifications
 * - Awards / Achievements
 * 
 * Each section is split into meaningful chunks that preserve context.
 */
public class TextChunker {

    private static final Logger logger = LoggerFactory.getLogger(TextChunker.class);

    // Default resume section headers (case-insensitive patterns)
    // Can be customized via constructor
    private Pattern sectionHeaderPattern;

    private static final String DEFAULT_SECTION_PATTERNS = "^(SUMMARY|OBJECTIVE|PROFILE|" +
            "EXPERIENCE|WORK EXPERIENCE|PROFESSIONAL EXPERIENCE|EMPLOYMENT|CAREER HISTORY|" +
            "EDUCATION|ACADEMIC BACKGROUND|QUALIFICATIONS|" +
            "SKILLS|TECHNICAL SKILLS|CORE COMPETENCIES|EXPERTISE|TECHNOLOGIES|" +
            "PROJECTS|KEY PROJECTS|PERSONAL PROJECTS|" +
            "CERTIFICATIONS|CERTIFICATES|LICENSES|TRAINING|" +
            "AWARDS|ACHIEVEMENTS|HONORS|RECOGNITION|" +
            "PUBLICATIONS|RESEARCH|PAPERS|" +
            "VOLUNTEER|VOLUNTEERING|ACTIVITIES|COMMUNITY SERVICE|" +
            "LANGUAGES|LANGUAGE PROFICIENCY|" +
            "REFERENCES|CONTACT)\\s*$";

    // Job/education entry patterns (dates, company names)
    private static final Pattern ENTRY_PATTERN = Pattern.compile(
            "^[A-Z][\\w\\s,&.()-]+\\s+[-–—|]\\s+[A-Z][\\w\\s,&.()-]+\\s*$",
            Pattern.MULTILINE);

    private final int minChunkSize;
    private final int maxChunkSize;

    /**
     * Constructor with default chunk sizes and section patterns
     */
    public TextChunker() {
        this(100, 800, DEFAULT_SECTION_PATTERNS);
    }

    /**
     * Constructor with custom chunk sizes and default patterns
     * 
     * @param minChunkSize Minimum words per chunk
     * @param maxChunkSize Maximum words per chunk
     */
    public TextChunker(int minChunkSize, int maxChunkSize) {
        this(minChunkSize, maxChunkSize, DEFAULT_SECTION_PATTERNS);
    }

    /**
     * Constructor with custom chunk sizes and section patterns
     * 
     * @param minChunkSize   Minimum words per chunk
     * @param maxChunkSize   Maximum words per chunk
     * @param customPatterns Custom regex pattern for section headers
     *                       (pipe-separated)
     *                       Example: "SUMMARY|EXPERIENCE|EDUCATION|SKILLS"
     */
    public TextChunker(int minChunkSize, int maxChunkSize, String customPatterns) {
        this.minChunkSize = minChunkSize;
        this.maxChunkSize = maxChunkSize;
        this.sectionHeaderPattern = Pattern.compile(
                customPatterns,
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        logger.info("TextChunker initialized with min={}, max={} words", minChunkSize, maxChunkSize);
    }

    /**
     * Split resume text into semantic chunks.
     * 
     * Strategy:
     * 1. Detect section headers (Experience, Education, Skills, etc.)
     * 2. Split each section by sub-entries (jobs, degrees, projects)
     * 3. Ensure each chunk includes section context
     * 4. Respect min/max chunk sizes
     * 
     * @param text Full resume text
     * @return List of text chunks with preserved context
     */
    public List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            logger.warn("Empty text provided for chunking");
            return chunks;
        }

        // Normalize line breaks and clean text
        text = normalizeText(text);

        // Split into sections based on headers
        List<Section> sections = detectSections(text);

        if (sections.isEmpty()) {
            // No sections detected, chunk by paragraphs
            logger.info("No resume sections detected, using paragraph chunking");
            return chunkByParagraphs(text);
        }

        // Process each section
        for (Section section : sections) {
            List<String> sectionChunks = chunkSection(section);
            chunks.addAll(sectionChunks);
        }

        logger.info("Created {} chunks from resume text", chunks.size());
        return chunks;
    }

    /**
     * Detect resume sections based on header patterns
     */
    private List<Section> detectSections(String text) {
        List<Section> sections = new ArrayList<>();
        String[] lines = text.split("\n");

        String currentHeader = null;
        StringBuilder currentContent = new StringBuilder();
        int startLine = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Check if this line is a section header
            Matcher matcher = sectionHeaderPattern.matcher(line);

            if (matcher.find() && line.length() < 50) {
                // Save previous section
                if (currentHeader != null && currentContent.length() > 0) {
                    sections.add(new Section(currentHeader, currentContent.toString().trim()));
                }

                // Start new section
                currentHeader = line;
                currentContent = new StringBuilder();
                startLine = i;
            } else if (currentHeader != null) {
                // Add content to current section
                currentContent.append(line).append("\n");
            }
        }

        // Add last section
        if (currentHeader != null && currentContent.length() > 0) {
            sections.add(new Section(currentHeader, currentContent.toString().trim()));
        }

        logger.info("Detected {} resume sections", sections.size());
        return sections;
    }

    /**
     * Chunk a single section into smaller pieces
     */
    private List<String> chunkSection(Section section) {
        List<String> chunks = new ArrayList<>();
        String content = section.content;

        // For experience/education sections, split by entries
        if (isExperienceOrEducation(section.header)) {
            chunks.addAll(chunkByEntries(section.header, content));
        } else {
            // For other sections, chunk by size
            chunks.addAll(chunkBySize(section.header, content));
        }

        return chunks;
    }

    /**
     * Check if section is Experience or Education
     */
    private boolean isExperienceOrEducation(String header) {
        String normalized = header.toLowerCase();
        return normalized.contains("experience") ||
                normalized.contains("education") ||
                normalized.contains("employment");
    }

    /**
     * Split experience/education section by individual entries
     */
    private List<String> chunkByEntries(String header, String content) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = content.split("\n\n+");

        StringBuilder currentChunk = new StringBuilder();
        currentChunk.append(header).append("\n");

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (trimmed.isEmpty())
                continue;

            int wordCount = countWords(currentChunk.toString() + "\n" + trimmed);

            if (wordCount > maxChunkSize && currentChunk.length() > header.length()) {
                // Save current chunk and start new one
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
                currentChunk.append(header).append("\n");
            }

            currentChunk.append(trimmed).append("\n");
        }

        // Add remaining content
        if (currentChunk.length() > header.length()) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * Chunk section by size constraints
     */
    private List<String> chunkBySize(String header, String content) {
        List<String> chunks = new ArrayList<>();
        String fullText = header + "\n" + content;

        int wordCount = countWords(fullText);

        if (wordCount <= maxChunkSize) {
            // Section fits in one chunk
            chunks.add(fullText);
        } else {
            // Split into multiple chunks
            String[] sentences = content.split("(?<=[.!?])\\s+");
            StringBuilder currentChunk = new StringBuilder();
            currentChunk.append(header).append("\n");

            for (String sentence : sentences) {
                int chunkWords = countWords(currentChunk.toString() + " " + sentence);

                if (chunkWords > maxChunkSize && currentChunk.length() > header.length()) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                    currentChunk.append(header).append("\n");
                }

                currentChunk.append(sentence).append(" ");
            }

            if (currentChunk.length() > header.length()) {
                chunks.add(currentChunk.toString().trim());
            }
        }

        return chunks;
    }

    /**
     * Fallback: chunk by paragraphs when no sections detected
     */
    private List<String> chunkByParagraphs(String text) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\n\n+");

        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (trimmed.isEmpty())
                continue;

            int wordCount = countWords(currentChunk.toString() + "\n" + trimmed);

            if (wordCount > maxChunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }

            currentChunk.append(trimmed).append("\n\n");
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * Normalize text: standardize line breaks, remove excessive whitespace
     */
    private String normalizeText(String text) {
        // Standardize line breaks
        text = text.replaceAll("\r\n", "\n");
        text = text.replaceAll("\r", "\n");

        // Remove excessive whitespace
        text = text.replaceAll("[ \t]+", " ");
        text = text.replaceAll("\n{3,}", "\n\n");

        return text.trim();
    }

    /**
     * Count words in text
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    /**
     * Inner class to represent a resume section
     */
    private static class Section {
        String header;
        String content;

        Section(String header, String content) {
            this.header = header;
            this.content = content;
        }
    }
}