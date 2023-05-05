package com.ivodam.finalpaper.edast.controller;


import com.ivodam.finalpaper.edast.entity.RegistryRequest;
import com.ivodam.finalpaper.edast.entity.RequestForm;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.RegistryRequestService;
import com.ivodam.finalpaper.edast.service.RequestFormService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class RequestController {

    private final RegistryRequestService registryRequestService;
    private final RequestFormService requestFormService;

    @GetMapping("/styles/request/{requestId}")
    public Optional<RequestForm> getRequestFormById(@PathVariable UUID requestId) {
        return requestFormService.findByRequestId(requestId);
    }

    @PostMapping("/styles/request")
    public RegistryRequest create(@RequestBody RegistryRequest request) throws AppException {
        return registryRequestService.saveRequest(request);
    }

    @GetMapping("/styles/request")
    public ResponseEntity<List<RequestForm>> findAll() {
        var requests = requestFormService.findAll();
        return ResponseEntity.ok(requests);
    }
}
