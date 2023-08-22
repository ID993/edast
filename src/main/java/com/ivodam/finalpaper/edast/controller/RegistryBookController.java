package com.ivodam.finalpaper.edast.controller;


import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.UserService;
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
public class RegistryBookController {


    private final RegistryBookService registryBookService;
    private final UserService userService;

    @GetMapping("/requests/all")
    public String allRequests(@RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "classNumber") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortOrder,
                              Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchByClassNumberOrUserOrEmployee(keyword, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "admin-all-requests";
    }

    //Employee
    @GetMapping("/requests/all/{userId}")
    public String allRequestsOfEmployee(@PathVariable UUID userId,
                                        @RequestParam(defaultValue = "") String keyword,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "6") int size,
                                        @RequestParam(defaultValue = "classNumber") String sortBy,
                                        @RequestParam(defaultValue = "asc") String sortOrder,
                                        Model model) throws AppException {
        var user = userService.findById(userId);
        if(user.getRole().equals(Enums.Roles.ROLE_ADMIN)) {
            return "redirect:/requests/all";
        }
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.findByEmployeeIdAndKeyword(user.getId(), keyword, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "employee-all-requests";
    }


    @GetMapping("/requests/all/bdm/{employeeId}")
    public String allBdmRequestsOfEmployee(@PathVariable UUID employeeId,
                                           @RequestParam(defaultValue = "") String keyword,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "6") int size,
                                           @RequestParam(defaultValue = "classNumber") String sortBy,
                                           @RequestParam(defaultValue = "asc") String sortOrder,
                                           Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllBdmByClassNumberOrUser(keyword, employeeId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "registry/employee-all-bdm-requests";
    }

    @GetMapping("/requests/all/work/{employeeId}")
    public String allWorkRequestsOfEmployee(@PathVariable UUID employeeId,
                                            @RequestParam(defaultValue = "") String keyword,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "6") int size,
                                            @RequestParam(defaultValue = "classNumber") String sortBy,
                                            @RequestParam(defaultValue = "asc") String sortOrder,
                                            Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllWorkByClassNumberOrUser(keyword, employeeId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "work/employee-all-work-requests";
    }

    @GetMapping("/requests/all/education/{employeeId}")
    public String allEducationRequestsOfEmployee(@PathVariable UUID employeeId,
                                                 @RequestParam(defaultValue = "") String keyword,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "6") int size,
                                                 @RequestParam(defaultValue = "classNumber") String sortBy,
                                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                                 Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllEducationByClassNumberOrUser(keyword, employeeId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "education/employee-all-education-requests";
    }

    @GetMapping("/requests/all/cadastral/{employeeId}")
    public String allCadastralRequestsOfEmployee(@PathVariable UUID employeeId,
                                                 @RequestParam(defaultValue = "") String keyword,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "6") int size,
                                                 @RequestParam(defaultValue = "classNumber") String sortBy,
                                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                                 Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllCadastralByClassNumberOrUser(keyword, employeeId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "cadastral/employee-all-cadastral-requests";
    }

    @GetMapping("/requests/all/special/{employeeId}")
    public String allSpecialRequestsOfEmployee(@PathVariable UUID employeeId,
                                               @RequestParam(defaultValue = "") String keyword,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "6") int size,
                                               @RequestParam(defaultValue = "classNumber") String sortBy,
                                               @RequestParam(defaultValue = "asc") String sortOrder,
                                               Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllSpecialByClassNumberOrUser(keyword, employeeId, pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return "special/employee-all-special-requests";
    }

    @GetMapping("/requests/unread/{userId}")
    public String allUnreadBdmRequestsOfEmployee(@PathVariable UUID userId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "6") int size,
                                                 @RequestParam(defaultValue = "classNumber") String sortBy,
                                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                                 Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.findByEmployeeIdAndRead(userId, false, pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "employee-all-unread-requests";
    }


    @GetMapping("/requests/reassign/{id}")
    public String reassignRequest(@PathVariable UUID id, Model model) {
        var employees = userService.findAllByRoleAndJobTitle("ROLE_EMPLOYEE", "Archivist");
        model.addAttribute("employees", employees);
        return "admin/reassign";
    }

    @PostMapping("/requests/reassign/{id}")
    public String reassignRequest(@PathVariable UUID id, @RequestParam UUID userId) throws AppException {
        var registryBook = registryBookService.findById(id);
        var employee = userService.findById(userId);
        registryBook.setEmployee(employee);
        registryBook.setRead(false);
        registryBookService.update(registryBook);
        return "redirect:/requests/all";
    }

    @RequestMapping("/search")
    public String searchAllRequests(@RequestParam(defaultValue = "") String keyword,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "6") int size,
                                    @RequestParam(defaultValue = "classNumber") String sortBy,
                                    @RequestParam(defaultValue = "asc") String sortOrder,
                                    Model model) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchByClassNumberOrUserOrEmployee(keyword, pageable);
        if(user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            requests = registryBookService.findByEmployeeIdAndKeyword(user.getId(), keyword, pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE) ? "employee-all-requests" : "admin-all-requests";
    }

    @RequestMapping("/search-bdm")
    public String searchBdm(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "6") int size,
                            @RequestParam(defaultValue = "classNumber") String sortBy,
                            @RequestParam(defaultValue = "asc") String sortOrder,
                            Model model) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "Registry", pageable);
        if(user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            requests = registryBookService.searchAllBdmByClassNumberOrUser(keyword, user.getId(), pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE) ? "registry/employee-all-bdm-requests" : "registry/admin-all-bdm-requests";
    }

    @RequestMapping("/search-work")
    public String searchWork(@RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "6") int size,
                             @RequestParam(defaultValue = "classNumber") String sortBy,
                             @RequestParam(defaultValue = "asc") String sortOrder,
                             Model model) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "Work", pageable);
        if(user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            requests = registryBookService.searchAllWorkByClassNumberOrUser(keyword, user.getId(), pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE) ? "work/employee-all-work-requests" : "work/admin-all-work-requests";
    }

    @RequestMapping("/search-education")
    public String searchEducation(@RequestParam(defaultValue = "") String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size,
                                  @RequestParam(defaultValue = "classNumber") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortOrder,
                                  Model model) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "Education", pageable);
        if(user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            requests = registryBookService.searchAllEducationByClassNumberOrUser(keyword, user.getId(), pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE) ? "education/employee-all-education-requests" : "education/admin-all-education-requests";
    }

    @RequestMapping("/search-cadastral")
    public String searchCadastral(@RequestParam(defaultValue = "") String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size,
                                  @RequestParam(defaultValue = "classNumber") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortOrder,
                                  Model model) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "Cadastral", pageable);
        if(user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            requests = registryBookService.searchAllCadastralByClassNumberOrUser(keyword, user.getId(), pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE) ? "cadastral/employee-all-cadastral-requests" : "cadastral/admin-all-cadastral-requests";
    }

    @RequestMapping("/search-special")
    public String searchSpecial(@RequestParam(defaultValue = "") String keyword,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "6") int size,
                                @RequestParam(defaultValue = "classNumber") String sortBy,
                                @RequestParam(defaultValue = "asc") String sortOrder,
                                Model model) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "Special", pageable);
        if(user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            requests = registryBookService.searchAllSpecialByClassNumberOrUser(keyword, user.getId(), pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("requests", requests);
        return user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE) ? "special/employee-all-special-requests" : "special/admin-all-special-requests";
    }


}
