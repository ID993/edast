package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class Index {

    @GetMapping("/")
    public String index(Principal principal){
        return principal != null ? "indexsignedin" : "index";
    }

}
