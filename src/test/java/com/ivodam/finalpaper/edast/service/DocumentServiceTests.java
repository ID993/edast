package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivodam.finalpaper.edast.entity.Document;
import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.repository.DocumentRepository;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTests {

  @Mock private DocumentRepository documentRepository;

  @Mock private FileStorageService fileStorageService;

  @Mock private ResponseService responseService;

  @InjectMocks private DocumentService documentService;

  @Test
  void storesSafeNameAndOpaqueStorageName() throws Exception {
    var responseId = UUID.randomUUID();
    var response = new Response();
    var file = new MockMultipartFile(
        "files", "C:\\fakepath\\report.pdf", "application/pdf",
        "content".getBytes(StandardCharsets.UTF_8));

    when(responseService.findById(responseId)).thenReturn(response);

    documentService.storeDocuments(responseId, new MultipartFile[] {file});

    var storageNameCaptor = ArgumentCaptor.forClass(String.class);
    var documentCaptor = ArgumentCaptor.forClass(Document.class);

    verify(fileStorageService)
        .save(any(MultipartFile.class), storageNameCaptor.capture());
    verify(documentRepository).save(documentCaptor.capture());

    var storageName = storageNameCaptor.getValue();
    var document = documentCaptor.getValue();

    assertThat(UUID.fromString(storageName)).isNotNull();
    assertThat(document.getName()).isEqualTo("report.pdf");
    assertThat(document.getPath()).isEqualTo(storageName);
    assertThat(document.getResponse()).isSameAs(response);
  }

  @Test
  void rejectsTraversalFilename() throws Exception {
    var responseId = UUID.randomUUID();
    var file =
        new MockMultipartFile("files", "../../secret.txt", "text/plain",
                              "content".getBytes(StandardCharsets.UTF_8));

    when(responseService.findById(responseId)).thenReturn(new Response());

    assertThatThrownBy(()
                           -> documentService.storeDocuments(
                               responseId, new MultipartFile[] {file}))
        .isInstanceOfSatisfying(AppException.class,
                                exception
                                -> assertThat(exception.getStatus())
                                       .isEqualTo(HttpStatus.BAD_REQUEST));

    verify(fileStorageService, never())
        .save(any(MultipartFile.class), anyString());
    verify(documentRepository, never()).save(any(Document.class));
  }

  @Test
  void skipsEmptyFiles() throws Exception {
    var responseId = UUID.randomUUID();
    var emptyFile =
        new MockMultipartFile("files", "empty.txt", "text/plain", new byte[0]);

    when(responseService.findById(responseId)).thenReturn(new Response());

    documentService.storeDocuments(responseId, new MultipartFile[] {emptyFile});

    verify(fileStorageService, never())
        .save(any(MultipartFile.class), anyString());
    verify(documentRepository, never()).save(any(Document.class));
  }
}