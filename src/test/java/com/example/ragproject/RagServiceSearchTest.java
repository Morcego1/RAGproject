package com.example.ragproject;

import com.example.ragproject.dto.QueryRequest;
import com.example.ragproject.dto.SearchResponse;
import com.example.ragproject.service.RagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit-test: семантический поиск RagService.search().
 * VectorStore and ChatClient are mocked - no database or Ollama needed.
 */
@ExtendWith(MockitoExtension.class)
class RagServiceSearchTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private RagService ragService;

    @Test
    void search_shouldReturnMappedResults() {
        Document chunk = Document.builder()
                .text("Test document about Spring AI")
                .metadata(Map.of("documentId", "id-1", "filename", "test.txt"))
                .score(0.91)
                .build();

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(chunk));

        List<SearchResponse> results = ragService.search(
                new QueryRequest("what is Spring AI", 5, 0.0));

        assertThat(results).hasSize(1);
        SearchResponse r = results.get(0);
        assertThat(r.getFilename()).isEqualTo("test.txt");
        assertThat(r.getDocumentId()).isEqualTo("id-1");
        assertThat(r.getContent()).isEqualTo("Test document about Spring AI");
        assertThat(r.getSimilarity()).isEqualTo(0.91);
    }
}