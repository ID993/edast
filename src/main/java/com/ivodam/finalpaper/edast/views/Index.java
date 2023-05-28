package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class Index {

    private final ResponseService responseService;
    private final RegistryBookService registryBookService;

    @GetMapping("/")
    public String index(Principal principal, HttpServletRequest request) {
        if(principal != null){
            var user = getLoggedInUsername();
            request.getSession().setAttribute("user", user);
            if(user.getRole() == Enums.Roles.ROLE_USER)
                request.getSession().setAttribute("msgCount", responseService.countByRead(false, user.getId()));
            else if(user.getRole() == Enums.Roles.ROLE_EMPLOYEE)
                request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
            return "home";
        }
        return "index";
    }



    private User getLoggedInUsername(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
