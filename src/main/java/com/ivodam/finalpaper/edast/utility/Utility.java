package com.ivodam.finalpaper.edast.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.Document;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@AllArgsConstructor
public class Utility {

    private final DocumentService documentService;

    public byte[] createZipFile(UUID id) throws IOException {
        var zipFile = File.createTempFile("download", ".zip");
        var zipOutput = new FileOutputStream(zipFile);
        var zipOutputStream = new ZipOutputStream(zipOutput);
        zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
        var files = documentService.findAllByResponseId(id);
        try {
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
        } finally {
            zipOutputStream.close();
        }
        var zipContent = new byte[(int) zipFile.length()];
        try (var inputStream = new FileInputStream(zipFile)) {
            inputStream.read(zipContent);
        }
        zipFile.delete();
        return zipContent;
    }

    public void messageConverter(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).setSupportedMediaTypes(
                        Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
            }
        }
    }

    public Object getRestUsersData(String endpoint, WebRequest request, Class<?> classType) {

        var sessionCookie = "JSESSIONID=" + request.getSessionId();
        String url = "http://localhost:8080/" + endpoint;
        RestTemplate restTemplate = new RestTemplate();
        messageConverter(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        var response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, classType);
        return response.getBody();
    }

    public void postRestUsersData(String endpoint, WebRequest request, Class<?> classType, long id, Enums.Roles role) {
        var sessionCookie = "JSESSIONID=" + request.getSessionId();
        var user = (UserDto) getRestUsersData("users/" + id, request, UserDto.class);
        user.setRole(role);
        String postUrl = "http://localhost:8080/" + endpoint;

        RestTemplate restTemplate = new RestTemplate();
        messageConverter(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(user, headers);
        restTemplate.exchange(postUrl, HttpMethod.POST, requestEntity, classType);
    }

    public void readAndPrintJson() throws IOException {
        String jsonPath = "C:\\Users\\ivoda\\Desktop\\edast\\src\\main\\resources\\static\\json\\archive.json";
        String outputPath = "C:\\Users\\ivoda\\Desktop\\edast\\src\\main\\resources\\static\\json\\newArchive.json";
        // Read the contents of the file into a String
        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)));

        // Print the JSON content to the console
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonContent);

        ObjectNode outputJsonNode = objectMapper.createObjectNode();
        // Iterate over the fields of the JSON object and translate their keys
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            String translatedKey = translate(field.getKey()); // replace with your translation function
            JsonNode value = field.getValue();
            // do something with the translated key and value
            outputJsonNode.set(translatedKey, value);
        }
        objectMapper.writeValue(Paths.get(outputPath).toFile(), outputJsonNode);
    }

    private static String translate(String key) {
        if (Objects.equals(key, "Signatura"))
            return "Signature";
        else if ("Naziv".equals(key))
            return "Title";
        else if ("Od".equals(key))
            return "From";
        else if ("Do".equals(key))
            return "To";
        else if ("Klasa".equals(key))
            return "Class";
        else
            return "Type";
    }

}
