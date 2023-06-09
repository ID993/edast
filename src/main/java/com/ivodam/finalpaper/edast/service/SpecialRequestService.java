package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.SpecialRequest;
import com.ivodam.finalpaper.edast.repository.SpecialRequestRepository;
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
public class SpecialRequestService {



    private final SpecialRequestRepository specialRequestRepository;

    private final RegistryBookService registryBookService;

    public SpecialRequest saveRequest(SpecialRequest specialRequest) {
        specialRequest.setRead(false);
        specialRequest.setCompleted(false);
        specialRequest.setDateCreated(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        return specialRequestRepository.save(specialRequest);
    }

    public SpecialRequest findById(UUID id) {
        return specialRequestRepository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        var registryBook = registryBookService.findByRequestId(id);
        //var specialRequest = specialRequestRepository.findById(id).get();
        if (registryBook != null) {
            registryBookService.deleteById(registryBook.getId());
        }
        //specialRequest.setUser(null);
        specialRequestRepository.deleteById(id);
    }

    public void updateRequest(SpecialRequest specialRequest) {
        specialRequestRepository.save(specialRequest);
    }

    public void readRequest(SpecialRequest specialRequest) {
        specialRequest.setRead(true);
        specialRequestRepository.save(specialRequest);
    }

    public List<SpecialRequest> findAll() {
        return specialRequestRepository.findAll();
    }

    public List<SpecialRequest> findAllByUserId(UUID userId) {
        return specialRequestRepository.findAllByUserId(userId);
    }

    public Page<SpecialRequest> searchAllByKeyword(String keyword, UUID userId, Pageable pageable) {
        return specialRequestRepository.searchAllByKeyword(keyword, userId, pageable);
    }
}
