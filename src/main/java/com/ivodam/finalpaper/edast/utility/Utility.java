package com.ivodam.finalpaper.edast.utility;

import com.ivodam.finalpaper.edast.entity.Document;
import com.ivodam.finalpaper.edast.service.DocumentService;
import com.ivodam.finalpaper.edast.service.FileStorageService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@AllArgsConstructor
public class Utility {

  private final DocumentService documentService;
  private final FileStorageService fileStorageService;

  public Sort getSort(String sortBy, String sortOrder) {
    var sort = Sort.by(sortBy);

    if (sortOrder.equalsIgnoreCase("desc")) {
      sort = sort.descending();
    }

    return sort;
  }

  public byte[] createZipFile(UUID responseId) throws IOException {
    var outputStream = new ByteArrayOutputStream();

    try (var zipOutputStream = new ZipOutputStream(outputStream)) {
      zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);

      var documents = documentService.findAllByResponseId(responseId);

      var index = 1;

      for (Document document : documents) {
        var entryName = index++ + "-" + safeEntryName(document.getName());

        zipOutputStream.putNextEntry(new ZipEntry(entryName));

        try (var inputStream =
                 fileStorageService.load(document.getPath()).getInputStream()) {
          inputStream.transferTo(zipOutputStream);
        }

        zipOutputStream.closeEntry();
      }
    }

    return outputStream.toByteArray();
  }

  private String safeEntryName(String fileName) {
    var cleanedName = StringUtils.cleanPath(fileName == null ? "" : fileName);
    var safeName = StringUtils.getFilename(cleanedName);

    return StringUtils.hasText(safeName) ? safeName : "document";
  }
}