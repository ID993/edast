package com.ivodam.finalpaper.edast.views;


import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
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
public class RegistryBookView {

    private final BDMRequestService bdmRequestService;
    private final RegistryBookService registryBookService;
    private final UserService userService;

    @GetMapping("/requests/all")
    public String allRequests(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size,
                              @RequestParam(defaultValue = "classNumber") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortOrder,
                              Model model) {
        var sort = Sort.by(sortBy);
        if (sortOrder.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        var pageable = PageRequest.of(page, size, sort);
        var requests = registryBookService.findAllPaging(pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "all-requests";
    }

    //Employee
    @GetMapping("/requests/all/{userId}")
    public String allRequestsOfEmployee(@PathVariable UUID userId,
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
        var requests = registryBookService.findByEmployeeId(user.getId(), pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "all-requests";
    }


    @GetMapping("/requests/all/bdm/{employeeId}")
    public String allBdmRequestsOfEmployee(@PathVariable UUID employeeId,
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
        var requests = registryBookService.findByEmployeeIdAndRequestName(employeeId, "BDM", pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "employee-all-bdm-requests";
    }

    @GetMapping("/requests/all/work/{employeeId}")
    public String allWorkRequestsOfEmployee(@PathVariable UUID employeeId,
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
        var requests = registryBookService.findByEmployeeIdAndRequestName(employeeId, "Work", pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "work/employee-all-work-requests";
    }

    @GetMapping("/requests/all/education/{employeeId}")
    public String allEducationRequestsOfEmployee(@PathVariable UUID employeeId,
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
        var requests = registryBookService.findByEmployeeIdAndRequestName(employeeId, "Education", pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "education/employee-all-education-requests";
    }

    @GetMapping("/requests/all/cadastral/{employeeId}")
    public String allCadastralRequestsOfEmployee(@PathVariable UUID employeeId,
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
        var requests = registryBookService.findByEmployeeIdAndRequestName(employeeId, "Cadastral", pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "cadastral/employee-all-cadastral-requests";
    }

    @GetMapping("/requests/all/special/{employeeId}")
    public String allSpecialRequestsOfEmployee(@PathVariable UUID employeeId,
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
        var requests = registryBookService.findByEmployeeIdAndRequestName(employeeId, "Special", pageable);
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
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
        return "all-unread-requests";
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

    @RequestMapping("/search")
    public String searchAllRequests(@RequestParam String keyword,
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
            requests = registryBookService.searchByClassNumberOrUser(keyword, user.getId(), pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("requests", requests);
        return "all-requests";
    }

    @RequestMapping("/search-bdm")
    public String searchBdm(@RequestParam String keyword,
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
        var requests = registryBookService.searchAllByClassNumberOrUserOrEmployeeAndRequestName(keyword, "BDM", pageable);
        if(user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
            requests = registryBookService.searchAllBdmByClassNumberOrUser(keyword, user.getId(), pageable);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("requests", requests);
        return "employee-all-bdm-requests";
    }

    @RequestMapping("/search-work")
    public String searchWork(@RequestParam String keyword,
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
        model.addAttribute("requests", requests);
        return "work/employee-all-work-requests";
    }

    @RequestMapping("/search-education")
    public String searchEducation(@RequestParam String keyword,
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
        model.addAttribute("requests", requests);
        return "education/employee-all-education-requests";
    }

    @RequestMapping("/search-cadastral")
    public String searchCadastral(@RequestParam String keyword,
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
        model.addAttribute("requests", requests);
        return "cadastral/employee-all-cadastral-requests";
    }

    @RequestMapping("/search-special")
    public String searchSpecial(@RequestParam String keyword,
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
        model.addAttribute("requests", requests);
        return "special/employee-all-special-requests";
    }

//    @GetMapping("/paging")
//    public String search(@RequestParam(defaultValue = "0") int page,
//                         @RequestParam(defaultValue = "6") int size,
//                         @RequestParam(defaultValue = "classNumber") String sortBy,
//                         @RequestParam(defaultValue = "asc") String sortOrder,
//                         Model model) {
//        var sort = Sort.by(sortBy);
//        if (sortOrder.equalsIgnoreCase("desc")) {
//            sort = sort.descending();
//        }
//
//        var pageable = PageRequest.of(page, size, sort);
//        var resultPage = registryBookService.findAllPaging(pageable);
//
//        model.addAttribute("resultPage", resultPage);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("sortBy", sortBy);
//        model.addAttribute("sortOrder", sortOrder);
//        return "all-requests-test";
//    }






}
