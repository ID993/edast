package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.CadastralRequest;
import com.ivodam.finalpaper.edast.repository.CadastralRequestRepository;
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
public class CadastralRequestService {



    private final CadastralRequestRepository cadastralRequestRepository;

    private final RegistryBookService registryBookService;

    public CadastralRequest saveRequest(CadastralRequest cadastralRequest) {
        cadastralRequest.setRead(false);
        cadastralRequest.setCompleted(false);
        cadastralRequest.setDateCreated(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        return cadastralRequestRepository.save(cadastralRequest);
    }

    public CadastralRequest findById(UUID id) {
        return cadastralRequestRepository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        var registryBook = registryBookService.findByRequestId(id);
        if (registryBook != null) {
            registryBookService.deleteById(registryBook.getId());
        }
        cadastralRequestRepository.deleteById(id);
    }

    public void updateRequest(CadastralRequest cadastralRequest) {
        cadastralRequestRepository.save(cadastralRequest);
    }

    public void readRequest(CadastralRequest cadastralRequest) {
        cadastralRequest.setRead(true);
        cadastralRequestRepository.save(cadastralRequest);
    }

    public List<CadastralRequest> findAll() {
        return cadastralRequestRepository.findAll();
    }

    public List<CadastralRequest> findAllByUserId(UUID userId) {
        return cadastralRequestRepository.findAllByUserId(userId);
    }

    public Page<CadastralRequest> searchAllByKeyword(String keyword, UUID userId, Pageable pageable) {
        return cadastralRequestRepository.searchAllByKeyword(keyword, userId, pageable);
    }
}
