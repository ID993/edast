package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Arrays;

@Controller
@AllArgsConstructor
public class Index {

    private final ResponseService responseService;
    private final RegistryBookService registryBookService;
    private final UserService userService;

    @GetMapping("/")
    public String index(Principal principal, HttpServletRequest request, Model model) throws AppException {
        if(principal != null){
            var user = getLoggedInUsername();
            request.getSession().setAttribute("user", user);
            if(user.getRole() == Enums.Roles.ROLE_ADMIN) {
                var chartData = registryBookService.countByRequestName();
                for (var data : chartData) {
                    System.out.println(Arrays.toString(data));
                }
                model.addAttribute("chartData", chartData);
                model.addAttribute("total", registryBookService.count());
                model.addAttribute("totalUsers", userService.count());
                model.addAttribute("usersByRole", userService.countByRole());
                return "home/admin";
            }
//            else if(user.getRole() == Enums.Roles.ROLE_USER)
//                request.getSession().setAttribute("msgCount", responseService.countByRead(false, user.getId()));
            else if(user.getRole() == Enums.Roles.ROLE_EMPLOYEE) {
                request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
                request.getSession().setAttribute("total", registryBookService.countByEmployeeId(user.getId()));
                request.getSession().setAttribute("totalCompleted", registryBookService.countByEmployeeIdAndStatus(user.getId(), "Completed"));
                return "home/employee";
            }
            request.getSession().setAttribute("msgCount", responseService.countByRead(false, user.getId()));
            return "home/user";
        }
        return "home/index";
    }

        private User getLoggedInUsername(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
