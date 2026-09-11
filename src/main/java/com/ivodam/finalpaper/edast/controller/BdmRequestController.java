package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.entity.BDMRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.RequestAccessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class BdmRequestController {

  private final BDMRequestService bdmRequestService;
  private final RegistryBookService registryBookService;
  private final RequestAccessService requestAccessService;

  @GetMapping("/bdm-requests")
  public String requests(Model model) {
    var bdmRequest = new BDMRequest();
    model.addAttribute("bdmRequest", bdmRequest);
    return "registry/make-bdm-request";
  }

  @PostMapping("/bdm-requests")
  public String
  requests(@Valid @ModelAttribute("bdmRequest") BDMRequest bdmRequest,
           BindingResult result, @RequestParam String bdmSelection) {
    if (result.hasErrors()) {
      return "registry/make-bdm-request";
    }
    User user = (User)SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
    bdmRequest.setUser(user);
    bdmRequest.setRequestName("Registry");
    bdmRequest.setBdmSelection(bdmSelection);
    var request = bdmRequestService.saveRequest(bdmRequest);
    registryBookService.create(request.getId(), request.getRequestName(), user);
    return "redirect:/user-bdm-requests/all";
  }

  @GetMapping("/bdm-requests/all")
  public String
  allRequests(@RequestParam(defaultValue = "") String keyword,
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
    var requests = registryBookService
                       .searchAllByClassNumberOrUserOrEmployeeAndRequestName(
                           keyword, "Registry", pageable);
    model.addAttribute("currentPage", page);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("requests", requests);
    return "registry/admin-all-bdm-requests";
  }

  @GetMapping("/user-bdm-requests/all")
  public String
  allUserBdmRequests(@RequestParam(defaultValue = "") String keyword,
                     @RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "6") int size,
                     @RequestParam(defaultValue = "dateCreated") String sortBy,
                     @RequestParam(defaultValue = "asc") String sortOrder,
                     Model model) {
    var user = (User)SecurityContextHolder.getContext()
                   .getAuthentication()
                   .getPrincipal();
    var sort = Sort.by(sortBy);
    if (sortOrder.equalsIgnoreCase("desc")) {
      sort = sort.descending();
    }
    var pageable = PageRequest.of(page, size, sort);
    var bdmRequests =
        bdmRequestService.searchAllByKeyword(keyword, user.getId(), pageable);
    model.addAttribute("currentPage", page);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("bdmRequests", bdmRequests);
    return "registry/user-bdm-requests";
  }

  @PostMapping("/bdm-requests/delete/{id}")
  public String deleteById(@PathVariable UUID id) throws AppException {
    var user = (User)SecurityContextHolder.getContext()
                   .getAuthentication()
                   .getPrincipal();
    bdmRequestService.deleteOwnedBy(id, user.getId());
    return "redirect:/user-bdm-requests/all";
  }

  @GetMapping("request/Registry/{requestId}")
  public String bdmRequestDetails(@PathVariable UUID requestId, Model model,
                                  HttpServletRequest request)
      throws AppException {
    var bdmRequest = bdmRequestService.findById(requestId);
    var user = (User)SecurityContextHolder.getContext()
                   .getAuthentication()
                   .getPrincipal();
    var registryBook = requestAccessService.requireAccess(requestId, user);
    model.addAttribute("request", bdmRequest);
    if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
      bdmRequestService.readRequest(bdmRequest);
      registryBookService.updateReadStatus(registryBook);
      request.getSession().setAttribute(
          "msgCount",
          registryBookService.countByEmployeeIdAndRead(user.getId(), false));
      return "registry/user-bdm-request-details";
    }
    return "registry/user-bdm-request-details";
  }

  @GetMapping("/search-bdm-requests")
  public String
  searchRequests(@RequestParam(defaultValue = "") String keyword,
                 @RequestParam(defaultValue = "0") int page,
                 @RequestParam(defaultValue = "6") int size,
                 @RequestParam(defaultValue = "dateCreated") String sortBy,
                 @RequestParam(defaultValue = "asc") String sortOrder,
                 Model model) {
    var user = (User)SecurityContextHolder.getContext()
                   .getAuthentication()
                   .getPrincipal();
    var sort = Sort.by(sortBy);
    if (sortOrder.equalsIgnoreCase("desc")) {
      sort = sort.descending();
    }
    var pageable = PageRequest.of(page, size, sort);
    var bdmRequests =
        bdmRequestService.searchAllByKeyword(keyword, user.getId(), pageable);
    model.addAttribute("currentPage", page);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("bdmRequests", bdmRequests);
    return "registry/user-bdm-requests";
  }
}
