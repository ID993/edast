package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.service.DocumentService;
import com.ivodam.finalpaper.edast.utility.Utility;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class DocumentView {

    private final Utility utility;

    @GetMapping("/storage/{filename}")
    public ResponseEntity<ByteArrayResource> getImages(@PathVariable String filename) throws IOException {
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        var file = new File("src/main/resources/static/storage/" + decodedFilename);
        var data = FileUtils.readFileToByteArray(file);
        var resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + decodedFilename)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length))
                .body(resource);
    }

    @GetMapping("/download/{responseId}")
    public ResponseEntity<byte[]> downloadFiles(@PathVariable UUID responseId) throws IOException {
        var zipContent = utility.createZipFile(responseId);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "edast.zip");
        return ResponseEntity.ok().headers(headers).body(zipContent);
    }
}
