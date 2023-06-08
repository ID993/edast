package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.CadastralRequest;
import com.ivodam.finalpaper.edast.entity.EducationRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.CadastralRequestService;
import com.ivodam.finalpaper.edast.service.EducationRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class CadastralRequestView {


    private final CadastralRequestService cadastralRequestService;
    private final RegistryBookService registryBookService;
    private final ResponseService responseService;



    @GetMapping("/cadastral-requests")
    public String cadastralRequests(Model model){
        model.addAttribute("cadastralRequest", new CadastralRequest());
        return "cadastral/make-cadastral-request";
    }

    @PostMapping("/cadastral-requests")
    public String cadastralRequests(@ModelAttribute CadastralRequest cadastralRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cadastralRequest.setUser(user);
        cadastralRequest.setRequestName("Cadastral");
        var request = cadastralRequestService.saveRequest(cadastralRequest);
        registryBookService.create(request.getId(), request.getRequestName(), user);
        return "redirect:/user-cadastral-requests/all/" + user.getId();
    }

    @GetMapping("/cadastral-requests/all")
    public String allCadastralRequests(Model model){
        model.addAttribute("requests", registryBookService.findByRequestName("Cadastral"));
        return "cadastral/admin-all-cadastral-requests";
    }

    @GetMapping("/user-cadastral-requests/all/{userId}")
    public String allUserCadastralRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("cadastralRequests", cadastralRequestService.findAllByUserId(userId));
        return "cadastral/user-cadastral-requests";
    }

    @GetMapping("/employee-cadastral-requests/all/{userId}")
    public String allEmployeeCadastralRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("cadastralRequests", cadastralRequestService.findAllByUserId(userId));
        return "cadastral/all-cadastral-requests";
    }
    @GetMapping("/cadastral-requests/delete/{id}")
    public String deleteById(@PathVariable UUID id) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cadastralRequestService.deleteById(id);
        return "redirect:/user-cadastral-requests/all/" + user.getId();
    }

    @GetMapping("request/Cadastral/{requestId}")
    public String employeeCadastralRequestDetails(@PathVariable UUID requestId, Model model, HttpServletRequest request) {
        var workRequest = cadastralRequestService.findById(requestId);
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("request", workRequest);
        if(user.getRole().equals(Enums.Roles.ROLE_USER)) {
            return "cadastral/user-cadastral-request-details";
        } else if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            cadastralRequestService.readRequest(workRequest);
            registryBookService.updateReadStatus(requestId);
            request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
            return "cadastral/employee-cadastral-request-details";
        }
        return "redirect:/cadastral-requests/all";
    }

    @RequestMapping("/search-cadastral-requests")
    public String searchRequests(@RequestParam String name, Model model) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("cadastralRequests", cadastralRequestService.searchAllByKeyword(name, user.getId()));
        return "cadastral/user-cadastral-requests";
    }
}
