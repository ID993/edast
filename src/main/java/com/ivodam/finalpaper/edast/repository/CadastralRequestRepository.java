package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.CadastralRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CadastralRequestRepository extends JpaRepository<CadastralRequest, UUID> {
        List<CadastralRequest> findAllByUserId(UUID userId);
        @Query("SELECT e FROM cadastral_request e WHERE e.user.id IN (SELECT u.id FROM users u WHERE u.id = :userId) AND (LOWER(e.cadastralSelection) LIKE %:keyword% OR LOWER(e.cadastralMunicipality) LIKE %:keyword%)")
        Page<CadastralRequest> searchAllByKeyword(String keyword, UUID userId, Pageable pageable);


}
