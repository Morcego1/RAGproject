package com.example.ragproject.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI автоматически поставляет только ChatClient.Builder,
 * но не готовый ChatClient. Этот бин закрывает разрыв.
 */
@Configuration
public class AiConfig {

    private static final String SYSTEM_PROMPT = """
            Ты — помощник по работе с технической документацией.
            Отвечай строго на основании предоставленного контекста.
            Если ответа в контексте нет — честно скажи об этом.
            Отвечай на языке вопроса.
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }
}
