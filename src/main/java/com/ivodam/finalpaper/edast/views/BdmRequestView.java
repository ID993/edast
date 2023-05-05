package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
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


    @GetMapping("/requests")
    public String requests(){
        return "requests";
    }

    @GetMapping("/requests/{requestName}")
    public String requests(@PathVariable String requestName, Model model){
        model.addAttribute("bdmRequest", new BDMRequest());
        return "make-bdm-request";
    }

    @PostMapping("/requests/{requestName}")
    public String requests(@PathVariable String requestName, @ModelAttribute BDMRequest bdmRequest) throws AppException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bdmRequest.setUser(user);
        bdmRequest.setRequestName(requestName);
        var request = bdmRequestService.saveRequest(bdmRequest);
        registryBookService.save(request.getId(), request.getRequestName());
        return "redirect:/requests/all";
    }

    @GetMapping("/requests/all")
    public String allRequests(Model model){
        model.addAttribute("bdmRequests", bdmRequestService.findAll());
        return "all-bdm-requests";
    }

    @GetMapping("requests/delete/{id}")
    public String deleteUserById(@PathVariable UUID id) {
        bdmRequestService.deleteById(id);
        return "redirect:/requests/all";
    }

    @GetMapping("/requests/{id}/response")
    public String getRespond(@PathVariable UUID id, Model model) {
        model.addAttribute("response", responseService.findByRequestId(id));
        return "response-of-request";
    }

    @GetMapping("/my-requests/{userId}")
    public String myRequests(@PathVariable UUID userId, Model model) {
        model.addAttribute("bdmRequests", bdmRequestService.findAllByUserId(userId));
        return "my-requests";
    }




}
