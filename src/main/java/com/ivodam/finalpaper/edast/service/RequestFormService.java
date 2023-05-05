package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.RegistryRequest;
import com.ivodam.finalpaper.edast.entity.RequestForm;
import com.ivodam.finalpaper.edast.repository.RequestFormRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestFormService {

    private final RequestFormRepository requestFormRepository;


    public Optional<RequestForm> findByRequestId(UUID requestId) {
        return requestFormRepository.findById(requestId);
    }

    public List<RequestForm> findByUserId(UUID userId) {
        return requestFormRepository.findByUserId(userId);
    }

    public List<RequestForm> findAll() {
        return requestFormRepository.findAll();
    }

}
