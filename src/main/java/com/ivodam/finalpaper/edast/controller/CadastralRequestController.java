package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.entity.CadastralRequest;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.CadastralRequestService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.RequestAccessService;
import com.ivodam.finalpaper.edast.service.ResponseService;
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
public class CadastralRequestController {

  private final CadastralRequestService cadastralRequestService;
  private final RegistryBookService registryBookService;
  private final RequestAccessService requestAccessService;
  private final ResponseService responseService;

  @GetMapping("/cadastral-requests")
  public String cadastralRequests(Model model) {
    model.addAttribute("cadastralRequest", new CadastralRequest());
    return "cadastral/make-cadastral-request";
  }

  @PostMapping("/cadastral-requests")
  public String
  cadastralRequests(@Valid @ModelAttribute CadastralRequest cadastralRequest,
                    BindingResult result, Model model) {
    if (result.hasErrors()) {
      return "cadastral/make-cadastral-request";
    } else if (cadastralRequest.getLandParcels().isEmpty() &&
               cadastralRequest.getBuildingParcels().isEmpty()) {
      model.addAttribute("message",
                         "Land parcels or building parcels must be entered!");
      return "cadastral/make-cadastral-request";
    }
    User user = (User)SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
    cadastralRequest.setUser(user);
    cadastralRequest.setRequestName("Cadastral");
    var request = cadastralRequestService.saveRequest(cadastralRequest);
    registryBookService.create(request.getId(), request.getRequestName(), user);
    return "redirect:/user-cadastral-requests/all";
  }

  @GetMapping("/cadastral-requests/all")
  public String allCadastralRequests(
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "6") int size,
      @RequestParam(defaultValue = "classNumber") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder, Model model) {
    var sort = Sort.by(sortBy);
    if (sortOrder.equalsIgnoreCase("desc")) {
      sort = sort.descending();
    }
    var pageable = PageRequest.of(page, size, sort);
    var requests = registryBookService
                       .searchAllByClassNumberOrUserOrEmployeeAndRequestName(
                           keyword, "Cadastral", pageable);
    model.addAttribute("currentPage", page);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("requests", requests);
    return "cadastral/admin-all-cadastral-requests";
  }

  @GetMapping("/user-cadastral-requests/all")
  public String allUserCadastralRequests(

      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "6") int size,
      @RequestParam(defaultValue = "dateCreated") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder, Model model) {

    var user = (User)SecurityContextHolder.getContext()
                   .getAuthentication()
                   .getPrincipal();
    var sort = Sort.by(sortBy);
    if (sortOrder.equalsIgnoreCase("desc")) {
      sort = sort.descending();
    }
    var pageable = PageRequest.of(page, size, sort);
    var cadastralRequests = cadastralRequestService.searchAllByKeyword(
        keyword, user.getId(), pageable);
    model.addAttribute("currentPage", page);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("cadastralRequests", cadastralRequests);
    return "cadastral/user-cadastral-requests";
  }

  @PostMapping("/cadastral-requests/delete/{id}")
  public String deleteById(@PathVariable UUID id) throws AppException {
    var user = (User)SecurityContextHolder.getContext()
                   .getAuthentication()
                   .getPrincipal();
    cadastralRequestService.deleteOwnedBy(id, user.getId());
    return "redirect:/user-cadastral-requests/all";
  }

  @GetMapping("request/Cadastral/{requestId}")
  public String cadastralRequestDetails(@PathVariable UUID requestId,
                                        Model model, HttpServletRequest request)
      throws AppException {
    var workRequest = cadastralRequestService.findById(requestId);
    var user = (User)SecurityContextHolder.getContext()
                   .getAuthentication()
                   .getPrincipal();
    var registryBook = requestAccessService.requireAccess(requestId, user);
    model.addAttribute("request", workRequest);
    if (user.getRole().equals(Enums.Roles.ROLE_EMPLOYEE)) {
      cadastralRequestService.readRequest(workRequest);
      registryBookService.updateReadStatus(registryBook);
      request.getSession().setAttribute(
          "msgCount",
          registryBookService.countByEmployeeIdAndRead(user.getId(), false));
      return "cadastral/user-cadastral-request-details";
    }
    return "cadastral/user-cadastral-request-details";
  }

  @GetMapping("/search-cadastral-requests")
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
    var cadastralRequests = cadastralRequestService.searchAllByKeyword(
        keyword, user.getId(), pageable);
    model.addAttribute("currentPage", page);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("cadastralRequests", cadastralRequests);
    return "cadastral/user-cadastral-requests";
  }
}
