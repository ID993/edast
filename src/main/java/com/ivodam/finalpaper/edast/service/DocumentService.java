package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.entity.Document;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.DocumentRepository;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class DocumentService {

  private final DocumentRepository documentRepository;
  private final FileStorageService fileStorageService;
  private final ResponseService responseService;

  public void storeDocuments(UUID responseId, MultipartFile[] files)
      throws IOException, AppException {

    var response = responseService.findById(responseId);

    for (var file : files) {
      if (file.isEmpty()) {
        continue;
      }

      var originalName = safeOriginalName(file);
      var storageName = UUID.randomUUID().toString();

      fileStorageService.save(file, storageName);
      documentRepository.save(
          new Document(originalName, storageName, response));
    }
  }

  public List<Document> findAllByResponseId(UUID responseId) {
    return documentRepository.findAllByResponseId(responseId);
  }

  private String safeOriginalName(MultipartFile file) throws AppException {

    var originalName = file.getOriginalFilename();

    if (!StringUtils.hasText(originalName)) {
      throw invalidFileName();
    }

    var cleanedName = StringUtils.cleanPath(originalName);

    if (cleanedName.contains("..")) {
      throw invalidFileName();
    }

    var fileName = StringUtils.getFilename(cleanedName);

    if (!StringUtils.hasText(fileName)) {
      throw invalidFileName();
    }

    return fileName;
  }

  private AppException invalidFileName() {
    return new AppException("Invalid file name", HttpStatus.BAD_REQUEST);
  }
}