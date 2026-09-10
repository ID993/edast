package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.DocumentService;
import com.ivodam.finalpaper.edast.service.MailService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.RequestAccessService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
public class ResponseController {

  private final ResponseService responseService;

  private final RegistryBookService registryBookService;

  private final MailService mailService;

  private final DocumentService documentService;

  private final RequestAccessService requestAccessService;

  @GetMapping("/responses/{requestId}")
  public String makeResponse(@PathVariable UUID requestId, Model model)
      throws AppException {

    var registryBook =
        requestAccessService.requireAssignedEmployee(requestId, currentUser());

    model.addAttribute("response", new Response());
    model.addAttribute("record", registryBook);
    return "responses/make-response";
  }

  @PostMapping("/responses/{requestId}")
  public String makeResponse(@PathVariable UUID requestId,
                             @ModelAttribute Response response,
                             @ModelAttribute("files") MultipartFile[] files)
      throws IOException, AppException {

    var registryBook =
        requestAccessService.requireAssignedEmployee(requestId, currentUser());

    var coverLetter = responseService.create(registryBook, response);
    registryBookService.updateRegistryBook(requestId);
    documentService.storeDocuments(coverLetter.getId(), files);

    mailService.sendEmailAttachment(coverLetter.getTitle(),
                                    coverLetter.getContent(),
                                    coverLetter.getEmployee().getEmail(),
                                    coverLetter.getUser().getEmail(), true);

    return "redirect:/";
  }

  @GetMapping("/response/request/{id}")
  public String getRespond(@PathVariable UUID id, Model model,
                           HttpServletRequest request) throws AppException {

    var user = currentUser();
    var registryBook = requestAccessService.requireAccess(id, user);
    var response = responseService.findByRegistryBookId(registryBook.getId());

    var isEmpty =
        documentService.findAllByResponseId(response.getId()).isEmpty();

    if (user.getRole() == Enums.Roles.ROLE_USER) {
      response.setRead(true);
      responseService.update(response);
      request.getSession().setAttribute(
          "msgCount", responseService.countByRead(false, user.getId()));
    }

    model.addAttribute("response", response);
    model.addAttribute("isEmpty", isEmpty);
    return "responses/response-of-request";
  }

  @GetMapping("/responses/all")
  public String userResponses(Model model) {
    var user = currentUser();
    model.addAttribute("responses",
                       responseService.findAllByUserId(user.getId()));

    return "responses/responses";
  }

  private User currentUser() {
    return (User)SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
  }
}
