package com.example.ragproject.service;

import com.example.ragproject.dto.QueryRequest;
import com.example.ragproject.dto.RagResponse;
import com.example.ragproject.dto.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public List<SearchResponse> search(QueryRequest userRequest) {
        log.debug("Поиск по запросу: {}, topK: {}", userRequest.getQuery(), userRequest.getTopK());

        var vsSearchRequest = SearchRequest.builder()
                .query(userRequest.getQuery())
                .topK(userRequest.getTopK())
                .similarityThreshold(userRequest.getSimilarityThreshold())
                .build();

        var results = vectorStore.similaritySearch(vsSearchRequest);

        return results.stream()
                .map(r -> new SearchResponse(
                        r.getId(),
                        r.getMetadata().get("documentId") != null
                                ? r.getMetadata().get("documentId").toString() : null,
                        r.getMetadata().get("filename") != null
                                ? r.getMetadata().get("filename").toString() : null,
                        r.getText(),
                        r.getScore(),
                        0))
                .sorted((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()))
                .collect(Collectors.toList());
    }

    public RagResponse ragQuery(QueryRequest userRequest) {
        log.debug("RAG-запрос: {}", userRequest.getQuery());

        var vsSearchRequest = SearchRequest.builder()
                .query(userRequest.getQuery())
                .topK(userRequest.getTopK())
                .similarityThreshold(userRequest.getSimilarityThreshold())
                .build();

        var results = vectorStore.similaritySearch(vsSearchRequest);

        if (results.isEmpty()) {
            return new RagResponse(
                    "По вашему запросу ничего не найдено в базе знаний.",
                    userRequest.getQuery(), 0, 0.0);
        }

        // Формируем контекст из чанков
        String context = results.stream()
                .map(r -> {
                    String filename = r.getMetadata().get("filename") != null
                            ? r.getMetadata().get("filename").toString() : "Неизвестный документ";
                    return String.format("[Документ: %s]\n%s", filename, r.getText());
                })
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = String.format("""
                Контекст:
                %s

                Вопрос: %s
                """, context, userRequest.getQuery());

        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        double avgSimilarity = results.stream()
                .mapToDouble(r -> r.getScore())
                .average()
                .orElse(0.0);

        return new RagResponse(
                answer,
                userRequest.getQuery(),
                results.size(),
                Math.round(avgSimilarity * 100.0) / 100.0);
    }
}
