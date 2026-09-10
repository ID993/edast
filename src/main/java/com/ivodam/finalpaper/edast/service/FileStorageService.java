package com.ivodam.finalpaper.edast.service;

import com.ivodam.finalpaper.edast.repository.FileStorageRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService implements FileStorageRepository {

  private final Path root;

  public FileStorageService(@Value("${app.storage.location:storage}")
                            String storageLocation) {

    this.root = Path.of(storageLocation).toAbsolutePath().normalize();
  }

  @Override
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException exception) {
      throw new IllegalStateException("Could not initialize document storage",
                                      exception);
    }
  }

  @Override
  public void save(MultipartFile file, String storageName) throws IOException {

    var destination = resolve(storageName);

    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, destination);
    }
  }

  @Override
  public Resource load(String storageName) {
    var storedFile = resolve(storageName);

    if (!Files.isRegularFile(storedFile) || !Files.isReadable(storedFile)) {
      throw new IllegalStateException("Stored file is unavailable");
    }

    try {
      return new UrlResource(storedFile.toUri());
    } catch (MalformedURLException exception) {
      throw new IllegalArgumentException("Invalid stored file name", exception);
    }
  }

  private Path resolve(String storageName) {
    if (storageName == null || storageName.isBlank()) {
      throw new IllegalArgumentException("Stored file name is required");
    }

    var resolved = root.resolve(storageName).normalize();

    if (!root.equals(resolved.getParent())) {
      throw new IllegalArgumentException("Invalid stored file name");
    }

    return resolved;
  }
}