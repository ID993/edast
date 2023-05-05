package com.ivodam.finalpaper.edast.service;


import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.RegistryRequest;
import com.ivodam.finalpaper.edast.repository.RegistryRequestRepository;
import com.ivodam.finalpaper.edast.repository.RequestFormRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistryRequestService {

    private final RegistryRequestRepository registryRequestRepository;
    private final RequestFormRepository requestFormRepository;


    public RegistryRequest saveRequest(RegistryRequest request) {
        return registryRequestRepository.save(request);
    }

    public RegistryRequest findById(UUID id) {
        return (RegistryRequest) requestFormRepository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        requestFormRepository.deleteById(id);
    }

    public void updateRequest(RegistryRequest request) {
        registryRequestRepository.save(request);
    }

    public List<RegistryRequest> findAll() {
        return registryRequestRepository.findAll();
    }

    public List<RegistryRequest> findAllByUserId(UUID userId) {
        var forms = requestFormRepository.findByUserId(userId);
        return forms.stream()
                .map(form -> registryRequestRepository.findById(form.getId()).orElse(null))
                .toList();
    }
}
