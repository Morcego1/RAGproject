package com.example.ragproject.service;

import com.example.ragproject.dto.UploadResponse;
import com.example.ragproject.entity.Document;
import com.example.ragproject.entity.DocumentChunk;
import com.example.ragproject.repository.DocumentRepository;
import com.example.ragproject.repository.ChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class DocumentUploadService {

    private static final Logger log = LoggerFactory.getLogger(DocumentUploadService.class);

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final VectorStore vectorStore;

    public DocumentUploadService(DocumentRepository documentRepository,
                                 ChunkRepository chunkRepository,
                                 VectorStore vectorStore) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.vectorStore = vectorStore;
    }

    @Transactional
    public UploadResponse uploadDocument(MultipartFile file) throws IOException {
        log.info("Загрузка файла: {}", file.getOriginalFilename());

        // 1. Сохраняем метаданные документа
        Document entity = new Document(
                file.getOriginalFilename(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize());

        entity = documentRepository.save(entity);
        log.info("Документ сохранён в БД: {} (id: {})", entity.getFilename(), entity.getId());

        // 2. Читаем содержимое
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (content.isBlank()) {
            throw new IllegalArgumentException("Файл пуст: " + file.getOriginalFilename());
        }

        // 3. Разбиваем на чанки с metadata (documentId + filename)
        org.springframework.ai.document.Document aiDoc =
                org.springframework.ai.document.Document.builder()
                        .text(content)
                        .metadata(Map.of(
                                "documentId", entity.getId(),
                                "filename", entity.getFilename()))
                        .build();

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<org.springframework.ai.document.Document> chunks = splitter.apply(List.of(aiDoc));

        log.info("Создано чанков: {}", chunks.size());

        // 4. Сохраняем чанки в JPA
        for (int index = 0; index < chunks.size(); index++) {
            org.springframework.ai.document.Document chunk = chunks.get(index);

            String metadataJson = String.format(
                    "{\"documentId\":\"%s\",\"filename\":\"%s\",\"chunkIndex\":\"%d\"}",
                    entity.getId(), entity.getFilename(), index);

            DocumentChunk chunkEntity = new DocumentChunk(
                    entity, entity.getId(), index, chunk.getText(), metadataJson);

            chunkEntity = chunkRepository.save(chunkEntity);
            log.debug("Чанк {} сохранён (id: {})", index, chunkEntity.getId());
        }

        // 5. Добавляем в векторное хранилище — оно само считает эмбеддинги
        vectorStore.add(chunks);

        log.info("Документ успешно обработан: {} чанков", chunks.size());

        return new UploadResponse(
                entity.getId(),
                entity.getFilename(),
                entity.getSize(),
                chunks.size(),
                "Документ успешно загружен и обработан");
    }

    @Transactional
    public void deleteDocument(String documentId) {
        log.info("Удаление документа: {}", documentId);
        chunkRepository.deleteByDocumentId(documentId);
        documentRepository.deleteById(documentId);
        log.info("Документ {} удалён", documentId);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAllByOrderByCreatedAtDesc();
    }

    public Document getDocument(String id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Документ не найден: " + id));
    }

    @Transactional
    public UploadResponse loadDocumentFromResource(Resource resource) throws IOException {
        log.info("Загрузка из ресурса: {}", resource.getFilename());

        // Идемпотентность — не загружаем дважды
        if (documentRepository.existsByFilename(resource.getFilename())) {
            log.info("Документ {} уже загружен, пропускаем", resource.getFilename());
            Document existing = documentRepository.findAllByOrderByCreatedAtDesc().stream()
                    .filter(d -> d.getFilename().equals(resource.getFilename()))
                    .findFirst()
                    .orElseThrow();
            return new UploadResponse(
                    existing.getId(),
                    existing.getFilename(),
                    existing.getSize(),
                    0,
                    "Документ уже загружен, пропущена повторная загрузка");
        }

        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        Document entity = new Document(
                resource.getFilename(),
                resource.getFilename(),
                "text/plain",
                content.length());

        entity = documentRepository.save(entity);

        org.springframework.ai.document.Document aiDoc =
                org.springframework.ai.document.Document.builder()
                        .text(content)
                        .metadata(Map.of(
                                "documentId", entity.getId(),
                                "filename", entity.getFilename()))
                        .build();

        TokenTextSplitter splitter = new TokenTextSplitter();
        List<org.springframework.ai.document.Document> chunks = splitter.apply(List.of(aiDoc));

        // Сохраняем чанки в JPA
        for (int index = 0; index < chunks.size(); index++) {
            org.springframework.ai.document.Document chunk = chunks.get(index);

            String metadataJson = String.format(
                    "{\"documentId\":\"%s\",\"filename\":\"%s\",\"chunkIndex\":\"%d\"}",
                    entity.getId(), entity.getFilename(), index);

            DocumentChunk chunkEntity = new DocumentChunk(
                    entity, entity.getId(), index, chunk.getText(), metadataJson);

            chunkRepository.save(chunkEntity);
        }

        // Добавляем в векторное хранилище — оно само считает эмбеддинги
        vectorStore.add(chunks);

        log.info("Документ из ресурса загружен: {} чанков", chunks.size());

        return new UploadResponse(
                entity.getId(),
                entity.getFilename(),
                entity.getSize(),
                chunks.size(),
                "Документ успешно загружен из ресурса");
    }
}
