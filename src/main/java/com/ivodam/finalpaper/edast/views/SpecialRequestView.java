package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.SpecialRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.service.SpecialRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class SpecialRequestView {


    private final SpecialRequestService specialRequestService;
    private final RegistryBookService registryBookService;
    private final ResponseService responseService;



    @GetMapping("/special-requests")
    public String specialRequests(Model model){
        model.addAttribute("specialRequest", new SpecialRequest());
        return "special/make-special-request";
    }

    @PostMapping("/special-requests")
    public String specialRequests(@ModelAttribute SpecialRequest specialRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        specialRequest.setUser(user);
        specialRequest.setRequestName("Special");
        var request = specialRequestService.saveRequest(specialRequest);
        registryBookService.create(request.getId(), request.getRequestName(), user);
        return "redirect:/user-special-requests/all/" + user.getId();
    }

    @GetMapping("/special-requests/all")
    public String allSpecialRequests(Model model){
        model.addAttribute("requests", registryBookService.findByRequestName("Special"));
        return "special/admin-all-special-requests";
    }

    @GetMapping("/user-special-requests/all/{userId}")
    public String allUserSpecialRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("specialRequests", specialRequestService.findAllByUserId(userId));
        return "special/user-special-requests";
    }

    @GetMapping("/employee-special-requests/all/{userId}")
    public String allEmployeeSpecialRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("specialRequests", specialRequestService.findAllByUserId(userId));
        return "special/all-special-requests";
    }
    @GetMapping("/special-requests/delete/{id}")
    public String deleteById(@PathVariable UUID id) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        specialRequestService.deleteById(id);
        return "redirect:/user-special-requests/all/" + user.getId();
    }

    @GetMapping("request/Special/{requestId}")
    public String employeeSpecialRequestDetails(@PathVariable UUID requestId, Model model, HttpServletRequest request) {
        var workRequest = specialRequestService.findById(requestId);
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("request", workRequest);
        if(user.getRole().equals(Enums.Roles.ROLE_USER)) {
            return "special/user-special-request-details";
        } else if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            specialRequestService.readRequest(workRequest);
            registryBookService.updateReadStatus(requestId);
            request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
            return "special/employee-special-request-details";
        }
        return "redirect:/special-requests/all";
    }

    @RequestMapping("/search-special-requests")
    public String searchRequests(@RequestParam String name, Model model) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("specialRequests", specialRequestService.searchAllByKeyword(name, user.getId()));
        return "special/user-special-requests";
    }

}
