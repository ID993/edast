package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class Index {

    @GetMapping("/")
    public String index(Principal principal, HttpServletRequest request) {
        if(principal != null){
            var user = getLoggedInUsername();
            request.getSession().setAttribute("user", user);
            return "index-signed-in";
        }
        return "index";
    }

    private User getLoggedInUsername(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
