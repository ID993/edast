package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String allRequests(@RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "classNumber") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortOrder,
                              Model model){
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "BDM", pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "admin-all-bdm-requests";
    }

    @GetMapping("/user-bdm-requests/all/{userId}")
    public String allUserBdmRequests(@PathVariable UUID userId,
                                     @RequestParam(defaultValue = "") String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "6") int size,
                                     @RequestParam(defaultValue = "dateCreated") String sortBy,
                                     @RequestParam(defaultValue = "asc") String sortOrder,
                                     Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var bdmRequests = bdmRequestService.searchAllByKeyword(keyword, userId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("bdmRequests", bdmRequests);
        return "user-bdm-requests";
    }

    @GetMapping("/employee-bdm-requests/all/{userId}")
    public String allEmployeeBdmRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("bdmRequests", bdmRequestService.findAllByUserId(userId));
        return "all-bdm-requests";
    }
    @GetMapping("/bdm-requests/delete/{id}")
    public String deleteById(@PathVariable UUID id) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bdmRequestService.deleteById(id);
        return "redirect:/user-bdm-requests/all/" + user.getId();
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

    @RequestMapping("/search-bdm-requests")
    public String searchRequests(@RequestParam(defaultValue = "") String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "6") int size,
                                 @RequestParam(defaultValue = "dateCreated") String sortBy,
                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                 Model model) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var bdmRequests = bdmRequestService.searchAllByKeyword(keyword, user.getId(), pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("bdmRequests", bdmRequests);
        return "user-bdm-requests";
    }
}
