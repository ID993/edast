package com.ivodam.finalpaper.edast.controller;

import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.entity.User;
import com.ivodam.finalpaper.edast.enums.Enums;
import com.ivodam.finalpaper.edast.exceptions.AppException;
import com.ivodam.finalpaper.edast.service.DocumentService;
import com.ivodam.finalpaper.edast.service.MailService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    private final RegistryBookService registryBookService;

    private final MailService mailService;

    private final DocumentService documentService;


    @GetMapping("/responses/{requestId}")
    public String makeResponse(@PathVariable UUID requestId, Model model) throws AppException {
        var registryBookRecord = registryBookService.findByRequestId(requestId);
        model.addAttribute("response", new Response());
        model.addAttribute("record", registryBookRecord);
        return "responses/make-response";
    }


    @PostMapping("/responses/{requestId}")
    public String makeResponse(@PathVariable UUID requestId,
                            @ModelAttribute Response response,
                            @ModelAttribute("files") MultipartFile[] files) throws IOException, AppException {
        var coverLetter = responseService.create(requestId, response);
        registryBookService.updateRegistryBook(requestId);
        documentService.storeDocuments(coverLetter.getId(), files);
        mailService.sendEmailAttachment(coverLetter.getTitle(), coverLetter.getContent(),
                coverLetter.getEmployee().getEmail(), coverLetter.getUser().getEmail(), true);
        return "redirect:/";
    }

    @GetMapping("/response/request/{id}")
    public String getRespond(@PathVariable UUID id, Model model, HttpServletRequest request) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var response = responseService.findByRequestId(id);
        var isEmpty = documentService.findAllByResponseId(response.getId()).isEmpty();
        System.out.println("\nIs empty: " + isEmpty);
        if(user.getRole().equals(Enums.Roles.ROLE_USER)) {
            response.setRead(true);
            responseService.update(response);
            request.getSession().setAttribute("msgCount", responseService.countByRead(false, user.getId()));
        }
        model.addAttribute("response", response);
        model.addAttribute("isEmpty", isEmpty);
        return "responses/response-of-request";
    }


    @GetMapping("/responses/all/{userId}")
    public String userResponses(@PathVariable UUID userId, Model model){
        model.addAttribute("responses", responseService.findAllByUserId(userId));
        return "responses/responses";
    }
}
