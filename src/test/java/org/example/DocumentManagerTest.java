package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.example.DocumentManager.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSaveDocumentWithNewId() {
        DocumentManager manager = new DocumentManager();

        Document newDoc = Document.builder()
                .title("Test Document")
                .content("This is a test document.")
                .author(new Author("author1","Test Author"))
                .build();

        Document savedDoc = manager.save(newDoc);
        assertNotNull(savedDoc.getId(), "Document should have a generated ID");

        assertEquals("Test Document", savedDoc.getTitle(), "Document title should be the same");
        assertEquals("This is a test document.", savedDoc.getContent(), "Document content should be the same");
        assertEquals("Test Author", savedDoc.getAuthor().getName(), "Document author should be the same");

    }



    @Test
    void testSaveDocumentWithExistingId() {
        Document document = Document.builder()
                .id("existing-id")
                .title("Existing Document")
                .content("Existing content")
                .author(DocumentManager.Author.builder().id("author2").name("Author Two").build())
                .build();

        documentManager.save(document);

        Optional<Document> savedDocument = documentManager.findById("existing-id");
        assertTrue(savedDocument.isPresent());
        assertEquals("Existing Document", savedDocument.get().getTitle());
    }

    @Test
    void testSearchDocumentsByTitlePrefix() {
        documentManager.save(Document.builder()
                .title("Test Document")
                .content("Some content")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .created(Instant.now())
                .build());
        documentManager.save(Document.builder()
                .title("Test Another Document")
                .content("Another content")
                .author(DocumentManager.Author.builder().id("author2").name("Author Two").build())
                .created(Instant.now())
                .build());

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("Test"))
                .build();

        List<Document> documents = documentManager.search(request);

        assertEquals(2, documents.size());
    }


    @Test
    void testSearchDocumentsByContent() {
        documentManager.save(Document.builder()
                .title("Test Document 1")
                .content("This document contains A")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .created(Instant.now())
                .build());

        documentManager.save(Document.builder()
                .title("Test Document 2")
                .content("This document contains B")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .created(Instant.now())
                .build());

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("contains A"))
                .build();

        List<Document> documents = documentManager.search(searchRequest);

        assertEquals(1, documents.size());
        assertTrue(documents.getFirst().getContent().contains("contains A"));
    }

    @Test
    void testSearchDocumentsByAuthor() {
        documentManager.save(Document.builder()
                .title("Document 1")
                .content("Content A")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())  // Ініціалізуємо автора
                .build());

        documentManager.save(Document.builder()
                .title("Document 2")
                .content("Content B")
                .author(DocumentManager.Author.builder().id("author2").name("Author Two").build())  // Ініціалізуємо автора
                .build());

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("author1"))
                .build();

        List<Document> documents = documentManager.search(searchRequest);

        assertEquals(1, documents.size());
        assertEquals("Document 1", documents.getFirst().getTitle());
    }

    @Test
    void testSearchDocumentsByDateRange() {
        Instant now = Instant.now();
        Instant oneDayAgo = now.minusSeconds(86400);

        documentManager.save(Document.builder()
                .title("Document 1")
                .content("Content A")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .created(now)
                .build());

        documentManager.save(Document.builder()
                .title("Document 2")
                .content("Content B")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .created(oneDayAgo)
                .build());

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .createdFrom(oneDayAgo)
                .createdTo(now)
                .build();

        List<Document> documents = documentManager.search(searchRequest);

        assertEquals(2, documents.size());
    }

    @Test
    void testFindById() {
        Document document = Document.builder()
                .title("Test Document")
                .content("This is a test content")
                .author(DocumentManager.Author.builder().id("author1").name("Author One").build())
                .build();

        Document savedDocument = documentManager.save(document);

        Optional<Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument.getId(), foundDocument.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Document> foundDocument = documentManager.findById("non-existing-id");
        assertFalse(foundDocument.isPresent());
    }
}
