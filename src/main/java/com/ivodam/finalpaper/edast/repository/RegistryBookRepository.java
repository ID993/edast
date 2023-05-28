package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryBookRepository extends JpaRepository<RegistryBook, UUID> {

    Optional<RegistryBook> findByRequestId(UUID requestId);

    List<RegistryBook> findByEmployeeId(UUID employeeId);

    List<RegistryBook> findByRequestName(String requestName);
    List<RegistryBook> findByEmployeeIdAndRequestName(UUID employeeId, String requestName);

    List<RegistryBook> findByOrderByReceivedDateDesc();

    List<RegistryBook> findByEmployeeIdOrderByReceivedDateDesc(UUID employeeId);

    List<RegistryBook> findByEmployeeIdAndReadOrderByReceivedDateDesc(UUID employeeId, boolean read);

    long countByRequestName(String requestName);

    long countByEmployeeIdAndRead(UUID employeeId, boolean read);

    long countByStatusAndEmployeeId(String status, UUID employeeId);


}
