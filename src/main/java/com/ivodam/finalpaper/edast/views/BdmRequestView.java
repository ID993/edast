package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class BdmRequestView {


    private final BDMRequestService bdmRequestService;
    private final RegistryBookService registryBookService;
    private final ResponseService responseService;



    @GetMapping("/bdm-requests")
    public String requests(Model model){
        model.addAttribute("bdmRequest", new BDMRequest());
        return "make-bdm-request";
    }

    @PostMapping("/bdm-requests")
    public String requests(@ModelAttribute BDMRequest bdmRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bdmRequest.setUser(user);
        bdmRequest.setRequestName("BDM");
        var request = bdmRequestService.saveRequest(bdmRequest);
        registryBookService.create(request.getId(), request.getRequestName(), user);
        return "redirect:/user-bdm-requests/all/" + user.getId();
    }

    @GetMapping("/bdm-requests/all")
    public String allRequests(Model model){
        model.addAttribute("requests", registryBookService.findByRequestName("BDM"));
        return "all-requests";
    }

    @GetMapping("/user-bdm-requests/all/{userId}")
    public String allUserBdmRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("bdmRequests", bdmRequestService.findAllByUserId(userId));
        return "user-bdm-requests";
    }

    @GetMapping("/employee-bdm-requests/all/{userId}")
    public String allEmployeeBdmRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("bdmRequests", bdmRequestService.findAllByUserId(userId));
        return "all-bdm-requests";
    }
    @GetMapping("/bdm-requests/delete/{id}")
    public String deleteById(@PathVariable UUID id) {
        bdmRequestService.deleteById(id);
        return "redirect:/user-bdm-requests/all/{userId}";
    }

    @GetMapping("request/BDM/{requestId}")
    public String employeeBdmRequestDetails(@PathVariable UUID requestId, Model model, HttpServletRequest request) {
        var bdmRequest = bdmRequestService.findById(requestId);
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("request", bdmRequest);
        if(user.getRole().equals(Enums.Roles.ROLE_USER)) {
            return "user-bdm-request-details";
        } else if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            bdmRequestService.readRequest(bdmRequest);
            registryBookService.updateReadStatus(requestId);
            request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
            return "employee-bdm-request-details";
        }
        return "redirect:/bdm-requests/all";
    }


}
