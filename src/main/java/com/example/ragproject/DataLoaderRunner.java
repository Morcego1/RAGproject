package com.example.ragproject;

import com.example.ragproject.service.DocumentUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DataLoaderRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoaderRunner.class);

    private final DocumentUploadService documentUploadService;

    public DataLoaderRunner(DocumentUploadService documentUploadService) {
        this.documentUploadService = documentUploadService;
    }

    @Override
    public void run(ApplicationArguments args) {
        loadSampleDocumentation();
    }

    private void loadSampleDocumentation() {
        ClassPathResource resource = new ClassPathResource("data/spring-ai-guide.txt");

        if (!resource.exists()) {
            log.warn("Файл примера документации не найден: data/spring-ai-guide.txt");
            return;
        }

        try {
            log.info("Загрузка примера документации при старте...");
            documentUploadService.loadDocumentFromResource(resource);
            log.info("Пример документации загружен успешно");
        } catch (Exception e) {
            // Приложение остаётся работоспособным: таблицы созданы, API доступен.
            // Загрузку можно повторить через POST /api/upload, когда Ollama запущен.
            log.error("Не удалось загрузить пример документации: {}. "
                    + "Приложение запущено, можно загрузить документы через POST /api/upload",
                    e.getMessage());
        }
    }
}
