package com.ivodam.finalpaper.edast.utility;

import com.ivodam.finalpaper.edast.entity.Document;
import com.ivodam.finalpaper.edast.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@AllArgsConstructor
public class Utility {

    private final DocumentService documentService;


    public Sort getSort(String sortBy, String sortOrder) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        return sort;
    }

    public byte[] createZipFile(UUID id) throws IOException {
        var zipFile = File.createTempFile("download", ".zip");
        var zipOutput = new FileOutputStream(zipFile);
        var zipOutputStream = new ZipOutputStream(zipOutput);
        try (zipOutputStream) {
            zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
            var files = documentService.findAllByResponseId(id);
            for (Document document : files) {
                var filePath = "src/main/resources/static/storage/" + document.getName();

                var file = new File(filePath);
                var fileContent = new byte[(int) file.length()];
                try (var inputStream = new FileInputStream(file)) {
                    inputStream.read(fileContent);
                }

                var zipEntry = new ZipEntry(document.getName());
                zipOutputStream.putNextEntry(zipEntry);

                zipOutputStream.write(fileContent);
                zipOutputStream.closeEntry();
            }
        }
        var zipContent = new byte[(int) zipFile.length()];
        try (var inputStream = new FileInputStream(zipFile)) {
            inputStream.read(zipContent);
        }
        zipFile.delete();
        return zipContent;
    }

}
