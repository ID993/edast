package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.entity.WorkRequest;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import com.ivodam.finalpaper.edast.service.WorkRequestService;
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
public class WorkRequestView {


    private final WorkRequestService workRequestService;
    private final RegistryBookService registryBookService;
    private final ResponseService responseService;



    @GetMapping("/work-requests")
    public String workRequests(Model model){
        model.addAttribute("workRequest", new WorkRequest());
        return "work/make-work-request";
    }

    @PostMapping("/work-requests")
    public String workRequests(@ModelAttribute WorkRequest workRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workRequest.setUser(user);
        workRequest.setRequestName("Work");
        var request = workRequestService.saveRequest(workRequest);
        registryBookService.create(request.getId(), request.getRequestName(), user);
        return "redirect:/user-work-requests/all/" + user.getId();
    }

    @GetMapping("/work-requests/all")
    public String allWorkRequests(@RequestParam(defaultValue = "") String keyword,
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
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "Work", pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "work/admin-all-work-requests";
    }

    @GetMapping("/user-work-requests/all/{userId}")
    public String allUserWorkRequests(@PathVariable UUID userId,
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
        var workRequests = workRequestService.searchAllByKeyword(keyword, userId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("workRequests", workRequests);
        return "work/user-work-requests";
    }

    @GetMapping("/employee-work-requests/all/{userId}")
    public String allEmployeeWorkRequests(@PathVariable UUID userId, Model model){
        model.addAttribute("workRequests", workRequestService.findAllByUserId(userId));
        return "work/all-work-requests";
    }
    @GetMapping("/work-requests/delete/{id}")
    public String deleteById(@PathVariable UUID id) {
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workRequestService.deleteById(id);
        return "redirect:/user-work-requests/all/" + user.getId();
    }

    @GetMapping("request/Work/{requestId}")
    public String employeeWorkRequestDetails(@PathVariable UUID requestId, Model model, HttpServletRequest request) {
        var workRequest = workRequestService.findById(requestId);
        var user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("request", workRequest);
        if(user.getRole().equals(Enums.Roles.ROLE_USER)) {
            return "work/user-work-request-details";
        } else if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            workRequestService.readRequest(workRequest);
            registryBookService.updateReadStatus(requestId);
            request.getSession().setAttribute("msgCount", registryBookService.countByEmployeeIdAndRead(user.getId(), false));
            return "work/employee-work-request-details";
        }
        return "redirect:/work-requests/all";
    }

    @RequestMapping("/search-work-requests")
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
        var workRequests = workRequestService.searchAllByKeyword(keyword, user.getId(), pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("workRequests", workRequests);
        return "work/user-work-requests";
    }
}
