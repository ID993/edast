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
import org.springframework.web.bind.annotation.*;
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

    public void postRestUsersData(String endpoint, WebRequest request, Class<?> classType, long id, String role) {
        var sessionCookie = "JSESSIONID=" + request.getSessionId();
        var user = (UserDto) getRestUsersData("users/" + id, request, UserDto.class);
        user.setRole(user.getRole()+ "," + role);
        String postUrl = "http://localhost:8080/" + endpoint;

        RestTemplate restTemplate = new RestTemplate();
        messageConverter(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(user, headers);
        restTemplate.exchange(postUrl, HttpMethod.POST, requestEntity, classType);
    }


    @GetMapping("/users/list")
    public String getAllUsers(Model model, WebRequest request) {
        model.addAttribute("users", getRestUsersData("users", request, UserDto[].class));
        return "users-list";
    }

    @GetMapping("/users/list/{id}")
    public String getUserById(Model model, WebRequest request, @PathVariable long id) {
        var user = (UserDto) getRestUsersData("users/" + id, request, UserDto.class);
        model.addAttribute("user", user);
        return "users-update";
    }

//    @PostMapping("/users/add-role/{id}")
//    public String postUsersById(@ModelAttribute UserDto userDto, WebRequest request, @PathVariable long id) {
//        postRestUsersData("users/add-role", request, UserDto.class, id);
//        return "redirect:/users/rest/{id}";
//    }

    @GetMapping("/users/add-admin/{id}")
    public String addAdmin(WebRequest request, @PathVariable long id) {
        postRestUsersData("users/add-role", request, UserDto.class, id, "ROLE_ADMIN");
        return "redirect:/users/list/{id}";
    }

    @GetMapping("/users/add-employee/{id}")
    public String addEmployee(WebRequest request, @PathVariable long id) {
        postRestUsersData("users/add-role", request, UserDto.class, id, "ROLE_EMPLOYEE");
        return "redirect:/users/list/{id}";
    }



    @GetMapping("/users/delete/{id}")
    public String deleteUserById(@PathVariable long id, WebRequest request) {
        var sessionCookie = "JSESSIONID=" + request.getSessionId();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        restTemplate.exchange("http://localhost:8080/users/" + id, HttpMethod.DELETE, requestEntity, UserDto.class);
        return "redirect:/users/list";
    }

}
