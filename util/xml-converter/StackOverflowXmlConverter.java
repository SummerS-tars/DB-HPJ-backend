package top.thesumst.llm_eval_backend.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.jsoup.Jsoup;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility to convert StackOverflow XML dump to CSV format for backend import
 * 
 * Usage:
 * java StackOverflowXmlConverter questions input.xml output_questions.csv
 * java StackOverflowXmlConverter answers input.xml output_answers.csv
 */
public class StackOverflowXmlConverter {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java StackOverflowXmlConverter <type> <input.xml> <output.csv>");
            System.out.println("  type: 'questions' or 'answers'");
            System.exit(1);
        }
        
        String type = args[0];
        String inputFile = args[1];
        String outputFile = args[2];
        
        try {
            if ("questions".equals(type)) {
                convertQuestions(inputFile, outputFile);
            } else if ("answers".equals(type)) {
                convertAnswers(inputFile, outputFile);
            } else {
                System.out.println("Invalid type. Use 'questions' or 'answers'");
                System.exit(1);
            }
            System.out.println("Conversion completed: " + outputFile);
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Convert StackOverflow questions XML to CSV
     * CSV format: title,content,tags,postId,score
     */
    public static void convertQuestions(String inputFile, String outputFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(inputFile));
        
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            
            // Write CSV header
            writer.println("title,content,tags,postId,score");
            
            NodeList rows = doc.getElementsByTagName("row");
            int processedCount = 0;
            int questionCount = 0;
            
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                
                // Only process questions (PostTypeId=1)
                String postTypeId = row.getAttribute("PostTypeId");
                if (!"1".equals(postTypeId)) {
                    continue;
                }
                
                questionCount++;
                
                try {
                    String id = row.getAttribute("Id");
                    String title = row.getAttribute("Title");
                    String body = row.getAttribute("Body");
                    String tags = row.getAttribute("Tags");
                    String score = row.getAttribute("Score");
                    
                    // Skip if essential fields are missing
                    if (title.isEmpty() || body.isEmpty()) {
                        continue;
                    }
                    
                    // Clean title and content (remove HTML tags)
                    title = cleanHtmlContent(title);
                    body = cleanHtmlContent(body);
                    
                    // Convert tags format: |java|spring| -> java,spring
                    tags = convertTagsFormat(tags);
                    
                    // Write CSV row with proper escaping
                    writer.println(String.format("\"%s\",\"%s\",\"%s\",%s,%s",
                        escapeCsvField(title),
                        escapeCsvField(body),
                        escapeCsvField(tags),
                        id,
                        score.isEmpty() ? "0" : score
                    ));
                    
                    processedCount++;
                    
                    if (processedCount % 1000 == 0) {
                        System.out.println("Processed " + processedCount + " questions...");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error processing question row " + i + ": " + e.getMessage());
                }
            }
            
            System.out.println("Total questions found: " + questionCount);
            System.out.println("Successfully processed: " + processedCount);
        }
    }
    
    /**
     * Convert StackOverflow answers XML to CSV
     * CSV format: rawQuestionId,content,postId,score
     * Note: rawQuestionId is derived from ParentId
     */
    public static void convertAnswers(String inputFile, String outputFile) throws Exception {
        // First pass: build mapping of question postId to database id
        Map<String, String> questionIdMapping = new HashMap<>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(inputFile));
        
        // Build question ID mapping (assuming questions are processed first)
        // For now, we'll use ParentId directly as rawQuestionId
        // In production, you'd need to query the database for the mapping
        
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            
            // Write CSV header
            writer.println("rawQuestionId,content,postId,score");
            
            NodeList rows = doc.getElementsByTagName("row");
            int processedCount = 0;
            int answerCount = 0;
            
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                
                // Only process answers (PostTypeId=2)
                String postTypeId = row.getAttribute("PostTypeId");
                if (!"2".equals(postTypeId)) {
                    continue;
                }
                
                answerCount++;
                
                try {
                    String id = row.getAttribute("Id");
                    String parentId = row.getAttribute("ParentId");
                    String body = row.getAttribute("Body");
                    String score = row.getAttribute("Score");
                    
                    // Skip if essential fields are missing
                    if (parentId.isEmpty() || body.isEmpty()) {
                        continue;
                    }
                    
                    // Clean content (remove HTML tags)
                    body = cleanHtmlContent(body);
                    
                    // Write CSV row with proper escaping
                    // Note: Using ParentId directly as rawQuestionId for now
                    // In production, you'd need to map this to the actual database ID
                    writer.println(String.format("%s,\"%s\",%s,%s",
                        parentId, // This should be mapped to rawQuestionId
                        escapeCsvField(body),
                        id,
                        score.isEmpty() ? "0" : score
                    ));
                    
                    processedCount++;
                    
                    if (processedCount % 1000 == 0) {
                        System.out.println("Processed " + processedCount + " answers...");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error processing answer row " + i + ": " + e.getMessage());
                }
            }
            
            System.out.println("Total answers found: " + answerCount);
            System.out.println("Successfully processed: " + processedCount);
        }
    }
    
    /**
     * Remove HTML tags from content
     */
    private static String cleanHtmlContent(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        // Use Jsoup for better HTML cleaning (you'll need to add jsoup dependency)
        // For now, using simple regex
        String cleaned = HTML_TAG_PATTERN.matcher(html).replaceAll("");
        
        // Decode common HTML entities
        cleaned = cleaned.replace("&lt;", "<")
                        .replace("&gt;", ">")
                        .replace("&amp;", "&")
                        .replace("&quot;", "\"")
                        .replace("&#xA;", "\n");
        
        return cleaned.trim();
    }
    
    /**
     * Convert tags from |tag1|tag2| format to tag1,tag2
     */
    private static String convertTagsFormat(String tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        
        // Remove leading and trailing |, then replace | with ,
        if (tags.startsWith("|")) {
            tags = tags.substring(1);
        }
        if (tags.endsWith("|")) {
            tags = tags.substring(0, tags.length() - 1);
        }
        
        return tags.replace("|", ",");
    }
    
    /**
     * Escape CSV field content (handle quotes and commas)
     */
    private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // Escape quotes by doubling them
        return field.replace("\"", "\"\"");
    }
} 