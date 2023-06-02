package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findAllByResponseId(UUID responseId);
}
