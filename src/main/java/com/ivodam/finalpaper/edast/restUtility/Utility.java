package com.ivodam.finalpaper.edast.restUtility;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.enums.Enums;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
public class Utility {

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




}
