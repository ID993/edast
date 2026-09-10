package com.ivodam.finalpaper.edast.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class FileStorageServiceTests {

  @TempDir Path temporaryDirectory;

  @Test
  void savesAndLoadsFileInsideConfiguredStorage() throws Exception {
    var storage = temporaryDirectory.resolve("storage");
    var service = new FileStorageService(storage.toString());
    var file = new MockMultipartFile(
        "files", "report.txt", "text/plain",
        "document content".getBytes(StandardCharsets.UTF_8));

    service.init();
    service.save(file, "generated-storage-name");

    assertThat(Files.readString(storage.resolve("generated-storage-name")))
        .isEqualTo("document content");

    try (var inputStream =
             service.load("generated-storage-name").getInputStream()) {
      assertThat(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8))
          .isEqualTo("document content");
    }
  }

  @Test
  void rejectsTraversalWhenSavingFile() {
    var storage = temporaryDirectory.resolve("storage");
    var service = new FileStorageService(storage.toString());
    var file =
        new MockMultipartFile("files", "report.txt", "text/plain",
                              "content".getBytes(StandardCharsets.UTF_8));

    service.init();

    assertThatThrownBy(() -> service.save(file, "../outside.txt"))
        .isInstanceOf(IllegalArgumentException.class);

    assertThat(temporaryDirectory.resolve("outside.txt")).doesNotExist();
  }

  @Test
  void rejectsTraversalWhenLoadingFile() {
    var service = new FileStorageService(
        temporaryDirectory.resolve("storage").toString());

    service.init();

    assertThatThrownBy(() -> service.load("../outside.txt"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}