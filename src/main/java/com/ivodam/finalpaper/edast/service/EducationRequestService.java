package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import com.ivodam.finalpaper.edast.repository.EducationRequestRepository;
import com.ivodam.finalpaper.edast.repository.WorkRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EducationRequestService {

    private final EducationRequestRepository educationRequestRepository;

    private final RegistryBookService registryBookService;

    public EducationRequest saveRequest(EducationRequest educationRequest) {
        educationRequest.setRead(false);
        educationRequest.setCompleted(false);
        educationRequest.setDateCreated(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        return educationRequestRepository.save(educationRequest);
    }

    public EducationRequest findById(UUID id) {
        return educationRequestRepository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        var registryBook = registryBookService.findByRequestId(id);
        if (registryBook != null) {
            registryBookService.deleteById(registryBook.getId());
        }
        educationRequestRepository.deleteById(id);
    }

    public void updateRequest(EducationRequest educationRequest) {
        educationRequestRepository.save(educationRequest);
    }

    public void readRequest(EducationRequest educationRequest) {
        educationRequest.setRead(true);
        educationRequestRepository.save(educationRequest);
    }

    public List<EducationRequest> findAll() {
        return educationRequestRepository.findAll();
    }

    public List<EducationRequest> findAllByUserId(UUID userId) {
        return educationRequestRepository.findAllByUserId(userId);
    }

    public List<EducationRequest> searchAllByKeyword(String keyword, UUID userId) {
        return educationRequestRepository.searchAllByKeyword(keyword, userId);
    }
}
