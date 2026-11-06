package com.example.ragproject;

import com.example.ragproject.dto.QueryRequest;
import com.example.ragproject.dto.RagResponse;
import com.example.ragproject.service.RagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit-test: RagService.ragQuery() когда чанки найдены - LLM вызывается
 * и возвращает ответ на основе контекста.
 */
@ExtendWith(MockitoExtension.class)
class RagServiceRagQueryTest {

    @Mock
    private VectorStore vectorStore;

    @Test
    void ragQuery_shouldAnswerFromContextWhenChunksFound() {
        Document chunk = Document.builder()
                .text("Refund policy: return within 14 days.")
                .metadata(Map.of("documentId", "id-1", "filename", "reglament.txt"))
                .score(0.82)
                .build();

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(chunk));

        // Deep stubs mock the whole chain: prompt().user().call().content()
        ChatClient deepClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        when(deepClient.prompt().user(anyString()).call().content())
                .thenReturn("Refund is possible within 14 days.");

        RagService service = new RagService(vectorStore, deepClient);

        RagResponse response = service.ragQuery(
                new QueryRequest("How to return a product?", 5, 0.0));

        assertThat(response.getAnswer()).isEqualTo("Refund is possible within 14 days.");
        assertThat(response.getQuery()).isEqualTo("How to return a product?");
        assertThat(response.getRetrievedChunks()).isEqualTo(1);
        assertThat(response.getSimilarity()).isEqualTo(0.82);
    }
}