package com.ivodam.finalpaper.edast.controller;


import com.ivodam.finalpaper.edast.entity.SpecialRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.service.SpecialRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class SpecialRequestController {


    private final SpecialRequestService specialRequestService;
    private final RegistryBookService registryBookService;
    private final ResponseService responseService;



    @GetMapping("/special-requests")
    public String specialRequests(Model model){
        model.addAttribute("specialRequest", new SpecialRequest());
        return "special/make-special-request";
    }

    @PostMapping("/special-requests")
    public String specialRequests(@Valid @ModelAttribute SpecialRequest specialRequest,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "special/make-special-request";
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        specialRequest.setUser(user);
        specialRequest.setRequestName("Special");
        var request = specialRequestService.saveRequest(specialRequest);
        registryBookService.create(request.getId(), request.getRequestName(), user);
        return "redirect:/user-special-requests/all/" + user.getId();
    }

    @GetMapping("/special-requests/all")
    public String allSpecialRequests(@RequestParam(defaultValue = "") String keyword,
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
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "Special", pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "special/admin-all-special-requests";
    }

    @GetMapping("/user-special-requests/all/{userId}")
    public String allUserSpecialRequests(@PathVariable UUID userId,
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
        var specialRequests = specialRequestService.searchAllByKeyword(keyword, userId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("specialRequests", specialRequests);
        return "special/user-special-requests";
    }

//    @GetMapping("/employee-special-requests/all/{userId}")
//    public String allEmployeeSpecialRequests(@PathVariable UUID userId, Model model){
//        model.addAttribute("specialRequests", specialRequestService.findAllByUserId(userId));
//        return "special/all-special-requests";
//    }
    @GetMapping("/special-requests/delete/{id}")
    public String deleteById(@PathVariable UUID id) throws AppException {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        specialRequestService.deleteById(id);
        return "redirect:/user-special-requests/all/" + user.getId();
    }

    @GetMapping("request/Special/{requestId}")
    public String specialRequestDetails(@PathVariable UUID requestId, Model model, HttpServletRequest request) {
        var workRequest = specialRequestService.findById(requestId);
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("request", workRequest);
        if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            specialRequestService.readRequest(workRequest);
            registryBookService.updateReadStatus(requestId);
            request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
            return "special/user-special-request-details";
        }
        return "special/user-special-request-details";
    }

    @RequestMapping("/search-special-requests")
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
        var specialRequests = specialRequestService.searchAllByKeyword(keyword, user.getId(), pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("specialRequests", specialRequests);
        return "special/user-special-requests";
    }

}
