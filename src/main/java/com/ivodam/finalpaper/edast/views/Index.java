package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class Index {

    @GetMapping("/")
    public String index(Principal principal, Model model) {

        if(principal != null){
            var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            getLoggedInUsername();

            model.addAttribute("user", user);
            return "index-signed-in";
        }
        return "index";
    }

    private void getLoggedInUsername(){
        var authentication =
                (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(authentication.getId() + "\n");
//        return authentication.getName();
    }
}
