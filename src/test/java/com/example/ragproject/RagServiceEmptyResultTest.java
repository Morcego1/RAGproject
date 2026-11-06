package com.example.ragproject;

import com.example.ragproject.dto.QueryRequest;
import com.example.ragproject.dto.RagResponse;
import com.example.ragproject.service.RagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit-test: RagService.ragQuery() когда ничего не найдено - LLM
 * не должен вызываться, возвращается служебное сообщение.
 */
@ExtendWith(MockitoExtension.class)
class RagServiceEmptyResultTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private RagService ragService;

    @Test
    void ragQuery_shouldNotCallLlmWhenNothingFound() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());

        RagResponse response = ragService.ragQuery(
                new QueryRequest("unknown question", 5, 0.0));

        assertThat(response.getAnswer())
                .isEqualTo("По вашему запросу ничего не найдено в базе знаний.");
        assertThat(response.getRetrievedChunks()).isZero();
        assertThat(response.getSimilarity()).isZero();

        // LLM не должен вызываться вообще
        verifyNoInteractions(chatClient);
    }
}