package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.service.MailService;
import com.ivodam.finalpaper.edast.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class Index {

    private MailService mailService;

    private UserService userService;

    @GetMapping("/")
    public String index(Principal principal) {

        return principal != null ? "index-signed-in" : "index";
    }

}