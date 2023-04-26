package com.ivodam.finalpaper.edast.views;

import com.ivodam.finalpaper.edast.dto.MailDto;
import com.ivodam.finalpaper.edast.entity.Response;
import com.ivodam.finalpaper.edast.service.BDMRequestService;
import com.ivodam.finalpaper.edast.service.MailService;
import com.ivodam.finalpaper.edast.service.RegistryBookService;
import com.ivodam.finalpaper.edast.service.ResponseService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class ResponseView {

    private final ResponseService responseService;

    private final BDMRequestService bdmRequestService;

    private final RegistryBookService registryBookService;

    private final MailService mailService;

    @GetMapping("/responses")
    public String responses(Model model){
        model.addAttribute("responses", responseService.findAll());
        return "responses";
    }

    @GetMapping("/responses/{requestId}")
    public String responses(@PathVariable UUID requestId, Model model){
        var registryBookRecord = registryBookService.findByRequestId(requestId);
        model.addAttribute("response", new Response());
        model.addAttribute("record", registryBookRecord);
        return "make-response";
    }

    @PostMapping("/responses/{requestId}")
    public String responses(@PathVariable UUID requestId,
                            @ModelAttribute Response response) throws MessagingException {

        var request = bdmRequestService.findById(requestId);
        var employee = registryBookService.findByRequestId(requestId).getEmployee();
        var mail = new MailDto();
        mail.setFrom(employee.getEmail());
        mail.setTo(request.getUser().getEmail());
        mail.setSubject(response.getTitle());
        mail.setMessage("Your request has been processed. Please check your responses.");
        response.setRequestId(requestId);
        response.setEmployee(employee);
        responseService.create(response);
        registryBookService.updateRegistryBook(requestId);
        mailService.sendRequest(mail);
        return "redirect:/requests/" + requestId + "/response";
    }

    //DODATI U REGISTRY BOOK USERID MANY TO ONE


}
