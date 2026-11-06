package com.example.ragproject.repository;

import com.example.ragproject.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    List<Document> findByFilenameContainingIgnoreCase(String filename);

    List<Document> findAllByOrderByCreatedAtDesc();

    boolean existsByFilename(String filename);
}
