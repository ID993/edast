package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.WorkRequest;
import com.ivodam.finalpaper.edast.repository.WorkRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WorkRequestService {



    private final WorkRequestRepository workRequestRepository;

    private final RegistryBookService registryBookService;

    public WorkRequest saveRequest(WorkRequest workRequest) {
        workRequest.setRead(false);
        workRequest.setCompleted(false);
        workRequest.setDateCreated(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        return workRequestRepository.save(workRequest);
    }

    public WorkRequest findById(UUID id) {
        return workRequestRepository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        var registryBook = registryBookService.findByRequestId(id);
        if (registryBook != null) {
            registryBookService.deleteById(registryBook.getId());
        }
        workRequestRepository.deleteById(id);
    }

    public void updateRequest(WorkRequest workRequest) {
        workRequestRepository.save(workRequest);
    }

    public void readRequest(WorkRequest workRequest) {
        workRequest.setRead(true);
        workRequestRepository.save(workRequest);
    }

    public List<WorkRequest> findAll() {
        return workRequestRepository.findAll();
    }

    public List<WorkRequest> findAllByUserId(UUID userId) {
        return workRequestRepository.findAllByUserId(userId);
    }

    public Page<WorkRequest> searchAllByKeyword(String keyword, UUID userId, Pageable pageable) {
        return workRequestRepository.searchAllByKeyword(keyword, userId, pageable);
    }
}
