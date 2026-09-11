package com.ivodam.finalpaper.edast.repository;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageRepository {

  void init();

  void save(MultipartFile file, String storageName) throws IOException;

  Resource load(String storageName);
}