package com.ivodam.finalpaper.edast.repository;

import com.ivodam.finalpaper.edast.entity.RegistryBook;
import com.ivodam.finalpaper.edast.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryBookRepository extends JpaRepository<RegistryBook, UUID> {

    Optional<RegistryBook> findByRequestId(UUID requestId);

    Page<RegistryBook> findByRequestName(String requestName, Pageable pageable);
    Page<RegistryBook> findByEmployeeIdAndRequestName(UUID employeeId, String requestName, Pageable pageable);

    List<RegistryBook> findByOrderByReceivedDateDesc();

    List<RegistryBook> findByEmployeeIdOrderByReceivedDateDesc(UUID employeeId);

    @NotNull Page<RegistryBook> findByEmployeeIdAndRead(UUID employeeId, boolean read, @NotNull Pageable pageable);

    long countByRequestName(String requestName);

    long countByEmployeeIdAndRead(UUID employeeId, boolean read);

    long countByStatusAndEmployeeId(String status, UUID employeeId);

    @Query("SELECT e FROM registry_book e WHERE e.employee.id IN (SELECT u.id FROM users u WHERE u.id = :employeeId) AND (LOWER(e.classNumber) LIKE %:keyword% OR LOWER(e.user.name) LIKE %:keyword%)")
    Page<RegistryBook> findByEmployeeIdAndKeyword(UUID employeeId, String keyword, Pageable pageable);
    @Query("SELECT e FROM registry_book e JOIN e.user u JOIN e.employee emp WHERE LOWER(e.classNumber) LIKE %:keyword% OR LOWER(u.name) LIKE %:keyword% OR LOWER(emp.name) LIKE %:keyword%")
    Page<RegistryBook> searchAllByClassNumberOrUserOrEmployee(String keyword, Pageable pageable);

    @Query("SELECT e FROM registry_book e WHERE e.requestName = :requestName AND (LOWER(e.classNumber) LIKE %:keyword% OR EXISTS (SELECT u FROM e.user u WHERE LOWER(u.name) LIKE %:keyword%) OR EXISTS (SELECT emp FROM e.employee emp WHERE LOWER(emp.name) LIKE %:keyword%))")
    Page<RegistryBook> searchAllByClassNumberOrUserOrEmployeeAndRequestName(String keyword, String requestName, Pageable pageable);

    @Query("SELECT e FROM registry_book e WHERE e.employee.id IN (SELECT u.id FROM users u WHERE u.id = :employeeId) AND e.requestName = 'BDM' AND (LOWER(e.classNumber) LIKE %:keyword% OR LOWER(e.user.name) LIKE %:keyword%)")
    Page<RegistryBook> searchAllBdmByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable);

    @Query("SELECT e FROM registry_book e WHERE e.employee.id IN (SELECT u.id FROM users u WHERE u.id = :employeeId) AND e.requestName = 'Work' AND (LOWER(e.classNumber) LIKE %:keyword% OR LOWER(e.user.name) LIKE %:keyword%)")
    Page<RegistryBook> searchAllWorkByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable);

    @Query("SELECT e FROM registry_book e WHERE e.employee.id IN (SELECT u.id FROM users u WHERE u.id = :employeeId) AND e.requestName = 'Education' AND (LOWER(e.classNumber) LIKE %:keyword% OR LOWER(e.user.name) LIKE %:keyword%)")
    Page<RegistryBook> searchAllEducationByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable);

    @Query("SELECT e FROM registry_book e WHERE e.employee.id IN (SELECT u.id FROM users u WHERE u.id = :employeeId) AND e.requestName = 'Cadastral' AND (LOWER(e.classNumber) LIKE %:keyword% OR LOWER(e.user.name) LIKE %:keyword%)")
    Page<RegistryBook> searchAllCadastralByClassNumberOrUser(String keyword, UUID employeeId, Pageable pageable);

    @Query("SELECT e FROM registry_book e WHERE e.employee.id IN (SELECT u.id FROM users u WHERE u.id = :employeeId) AND e.requestName = 'Special' AND (LOWER(e.classNumber) LIKE %:keyword% OR LOWER(e.user.name) LIKE %:keyword%)")
    @NotNull Page<RegistryBook> searchAllSpecialByClassNumberOrUser(String keyword, UUID employeeId, @NotNull Pageable pageable);

    @NotNull Page<RegistryBook> findAll(@NotNull Pageable pageable);
}
