package org.example;

import java.time.Instant;
import java.util.*;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc.
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */

public class DocumentManager {
    private final Map<String, Document> documents = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */

    public Document save(Document document) {
        if (document.getId() == null) {
            document = document.toBuilder().id(generateUniqueId()).build();
        }
        documents.put(document.getId(), document);
        System.out.println(document);

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */

    public List<Document> search(SearchRequest request) {

        return documents.values().stream()
                .filter(doc -> matches(doc, request))
                .collect(Collectors.toList());
    }

    private boolean matches(Document document, SearchRequest request) {
        boolean matches = true;

        if (request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty()) {
            if (document.getTitle() == null) {
                matches = false;
            } else {
                matches &= request.getTitlePrefixes().stream()
                        .anyMatch(prefix -> document.getTitle().startsWith(prefix));
            }
        }

        if (request.getContainsContents() != null && !request.getContainsContents().isEmpty()) {
            if (document.getContent() == null) {
                matches = false;
            } else {
                matches &= request.getContainsContents().stream()
                        .anyMatch(content -> document.getContent().contains(content));
            }
        }

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            if (document.getAuthor() == null || !request.getAuthorIds().contains(document.getAuthor().getId())) {
                matches = false;
            }
        }

        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            matches = false;
        }

        if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
            matches = false;
        }

        return matches;
    }
    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder(toBuilder = true)
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}

