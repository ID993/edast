package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.EducationRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.service.WorkRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class EducationRequestView {


    private final EducationRequestService educationRequestService;
    private final RegistryBookService registryBookService;
    private final ResponseService responseService;



    @GetMapping("/education-requests")
    public String educationRequests(Model model){
        model.addAttribute("educationRequest", new EducationRequest());
        return "education/make-education-request";
    }

    @PostMapping("/education-requests")
    public String educationRequests(@ModelAttribute EducationRequest educationRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        educationRequest.setUser(user);
        educationRequest.setRequestName("Education");
        var request = educationRequestService.saveRequest(educationRequest);
        registryBookService.create(request.getId(), request.getRequestName(), user);
        return "redirect:/user-education-requests/all/" + user.getId();
    }

    @GetMapping("/education-requests/all")
    public String allEducationRequests(Model model){
        model.addAttribute("requests", registryBookService.findByRequestName("Education"));
        return "education/admin-all-education-requests";
    }

    @GetMapping("/user-education-requests/all/{userId}")
    public String allUserEducationRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("educationRequests", educationRequestService.findAllByUserId(userId));
        return "education/user-education-requests";
    }

    @GetMapping("/employee-education-requests/all/{userId}")
    public String allEmployeeEducationRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("educationRequests", educationRequestService.findAllByUserId(userId));
        return "education/all-education-requests";
    }
    @GetMapping("/education-requests/delete/{id}")
    public String deleteById(@PathVariable UUID id) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        educationRequestService.deleteById(id);
        return "redirect:/user-education-requests/all/" + user.getId();
    }

    @GetMapping("request/Education/{requestId}")
    public String employeeEducationRequestDetails(@PathVariable UUID requestId, Model model, HttpServletRequest request) {
        var workRequest = educationRequestService.findById(requestId);
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("request", workRequest);
        if(user.getRole().equals(Enums.Roles.ROLE_USER)) {
            return "education/user-education-request-details";
        } else if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            educationRequestService.readRequest(workRequest);
            registryBookService.updateReadStatus(requestId);
            request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
            return "education/employee-education-request-details";
        }
        return "redirect:/education-requests/all";
    }

    @RequestMapping("/search-education-requests")
    public String searchRequests(@RequestParam String name, Model model) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("educationRequests", educationRequestService.searchAllByKeyword(name, user.getId()));
        return "education/user-education-requests";
    }
}
