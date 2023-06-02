package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.Document;
import com.ivodam.finalpaper.edast.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final FileStorageService fileStorageService;

    private final ResponseService responseService;

    public void storeDocuments(UUID id, MultipartFile[] files) throws IOException {
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
                "/src/main/resources/static/storage/";
        String generatedString = RandomStringUtils.random(8, true, true) + "-";
        var response = responseService.findById(id);
        for (MultipartFile file : files) {
            if(!file.isEmpty()) {
                String fileName = generatedString + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                fileStorageService.save(file, generatedString);
                documentRepository.save(new Document(fileName, baseUrl + fileName, response));
            }
        }
    }

    public List<Document> findAllByResponseId(UUID id) {
        return documentRepository.findAllByResponseId(id);
    }
}
