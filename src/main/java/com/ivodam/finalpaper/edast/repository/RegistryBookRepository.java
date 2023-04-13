package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistryBookRepository extends JpaRepository<RegistryBook, UUID> {

    Optional<RegistryBook> findByRequestId(UUID requestId);


    long countByRequestName(String requestName);

    long countByStatusAndEmployeeId(String status, UUID employeeId);


}
