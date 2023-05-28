package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class RegistryBookView {

    private final BDMRequestService bdmRequestService;
    private final RegistryBookService registryBookService;
    private final UserService userService;



    //Admin
    @GetMapping("/requests/all")
    public String allRequests(Model model) {
        model.addAttribute("requests", registryBookService.orderByReceivedDateDesc());
        return "all-requests";
    }

    //Employee
    @GetMapping("/requests/all/{userId}")
    public String allRequestsOfEmployee(@PathVariable UUID userId, Model model) throws AppException {
        var user = userService.findById(userId);
        if(user.getRole().equals(Enums.Roles.ROLE_ADMIN)) {
            return "redirect:/requests/all";
        }
        var requests = registryBookService.findByEmployeeIdOrderByReceivedDateDesc(user.getId());
        model.addAttribute("requests", requests);
        return "all-requests";
    }


    @GetMapping("/requests/all/bdm/{employeeId}")
    public String allBdmRequestsOfEmployee(@PathVariable UUID employeeId, Model model) {
        var requests = registryBookService.findByEmployeeIdAndRequestName(employeeId, "BDM");
        model.addAttribute("requests", requests);
        return "employee-all-bdm-requests";
    }

    @GetMapping("/requests/all/unread/{employeeId}")
    public String allUnreadBdmRequestsOfEmployee(@PathVariable UUID employeeId, Model model) {
        var requests = registryBookService.findByEmployeeIdAndReadOrderByReceivedDateDesc(employeeId, false);
        model.addAttribute("requests", requests);
        return "employee-all-bdm-requests";
    }


    @GetMapping("/requests/reassign/{id}")
    public String reassignRequest(@PathVariable UUID id, Model model) {
        var employees = userService.findAllByRoleAndJobTitle("ROLE_EMPLOYEE", "Archivist");
        model.addAttribute("employees", employees);
        return "reassign/reassign";
    }

    @PostMapping("/requests/reassign/{id}")
    public String reassignRequest(@PathVariable UUID id, @RequestParam UUID userId) throws AppException {
        var registryBook = registryBookService.findById(id);
        var employee = userService.findById(userId);
        registryBook.setEmployee(employee);
        registryBookService.update(registryBook);
        return "redirect:/requests/all";
    }

    //+ jos 4


}
