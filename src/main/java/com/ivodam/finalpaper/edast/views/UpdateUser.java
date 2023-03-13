package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
public class UpdateUser {

    public void messageConverter(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).setSupportedMediaTypes(
                        Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
            }
        }
    }

    @GetMapping("/users/rest")
    public String getUsers(Model model, WebRequest request) {

        var sessionCookie = "JSESSIONID=" + request.getSessionId();

        String url = "http://localhost:8080/users";
        RestTemplate restTemplate = new RestTemplate();
        messageConverter(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        var response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UserDto[].class);
//        var users = restTemplate.getForObject(url, UserDto[].class);
        model.addAttribute("users", response.getBody());

        return "userslist";
    }
}
