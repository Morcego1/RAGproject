package com.example.ragproject.controller;

import com.example.ragproject.dto.*;
import com.example.ragproject.entity.Document;
import com.example.ragproject.service.DocumentUploadService;
import com.example.ragproject.service.RagService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RagController {

    private final DocumentUploadService documentUploadService;
    private final RagService ragService;

    public RagController(DocumentUploadService documentUploadService, RagService ragService) {
        this.documentUploadService = documentUploadService;
        this.ragService = ragService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        UploadResponse response = documentUploadService.uploadDocument(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<List<SearchResponse>> search(@RequestBody QueryRequest request) {
        List<SearchResponse> results = ragService.search(request);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/rag")
    public ResponseEntity<RagResponse> ragQuery(@RequestBody QueryRequest request) {
        RagResponse response = ragService.ragQuery(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Map<String, String>> deleteDocument(@PathVariable String id) {
        documentUploadService.deleteDocument(id);
        return ResponseEntity.ok(Map.of("message", "Документ удалён"));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentUploadService.getAllDocuments());
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) {
        Document document = documentUploadService.getDocument(id);
        return ResponseEntity.ok(document);
    }
}
