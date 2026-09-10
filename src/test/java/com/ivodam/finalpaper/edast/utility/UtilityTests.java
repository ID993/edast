package com.ivodam.finalpaper.edast.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.Document;
import com.ivodam.finalpaper.edast.service.DocumentService;
import com.ivodam.finalpaper.edast.service.FileStorageService;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;

@ExtendWith(MockitoExtension.class)
class UtilityTests {

  @Mock private DocumentService documentService;

  @Mock private FileStorageService fileStorageService;

  @InjectMocks private Utility utility;

  @Test
  void createsZipUsingStorageAndSafeEntryNames() throws Exception {
    var responseId = UUID.randomUUID();

    var firstDocument = new Document();
    firstDocument.setName("report.txt");
    firstDocument.setPath("first-storage-id");

    var legacyUnsafeDocument = new Document();
    legacyUnsafeDocument.setName("../../secret.txt");
    legacyUnsafeDocument.setPath("second-storage-id");

    when(documentService.findAllByResponseId(responseId))
        .thenReturn(List.of(firstDocument, legacyUnsafeDocument));

    when(fileStorageService.load("first-storage-id"))
        .thenReturn(
            new ByteArrayResource("first".getBytes(StandardCharsets.UTF_8)));

    when(fileStorageService.load("second-storage-id"))
        .thenReturn(
            new ByteArrayResource("second".getBytes(StandardCharsets.UTF_8)));

    var zipBytes = utility.createZipFile(responseId);

    try (var zipInputStream =
             new ZipInputStream(new ByteArrayInputStream(zipBytes))) {

      var firstEntry = zipInputStream.getNextEntry();
      assertThat(firstEntry.getName()).isEqualTo("1-report.txt");
      assertThat(
          new String(zipInputStream.readAllBytes(), StandardCharsets.UTF_8))
          .isEqualTo("first");

      var secondEntry = zipInputStream.getNextEntry();
      assertThat(secondEntry.getName()).isEqualTo("2-secret.txt");
      assertThat(
          new String(zipInputStream.readAllBytes(), StandardCharsets.UTF_8))
          .isEqualTo("second");

      assertThat(zipInputStream.getNextEntry()).isNull();
    }
  }
}