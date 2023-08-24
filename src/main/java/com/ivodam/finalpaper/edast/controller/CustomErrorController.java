package com.ivodam.finalpaper.edast.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        int statusCode = (int) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 404) {
            model.addAttribute("errorStatus", "404");
            model.addAttribute("errorMessage", "Not found");
        } else if (statusCode == 403) {
            model.addAttribute("errorStatus", "403");
            model.addAttribute("errorMessage", "Forbidden");
        } else if (statusCode == 400) {
            model.addAttribute("errorStatus", "400");
            model.addAttribute("errorMessage", "Bad request");
        } else if (statusCode == 401) {
            model.addAttribute("errorStatus", "401");
            model.addAttribute("errorMessage", "Unauthorized");
        } else if (statusCode == 405) {
            model.addAttribute("errorStatus", "405");
            model.addAttribute("errorMessage", "Method not allowed");
        } else if (statusCode == 406) {
            model.addAttribute("errorStatus", "406");
            model.addAttribute("errorMessage", "Not acceptable");
        } else if (statusCode == 409) {
            model.addAttribute("errorStatus", "409");
            model.addAttribute("errorMessage", "Conflict");
        } else if (statusCode == 415) {
            model.addAttribute("errorStatus", "415");
            model.addAttribute("errorMessage", "Unsupported media type");
        } else if (statusCode == 422) {
            model.addAttribute("errorStatus", "422");
            model.addAttribute("errorMessage", "Unprocessable entity");
        } else if (statusCode == 429) {
            model.addAttribute("errorStatus", "429");
            model.addAttribute("errorMessage", "Too many requests");
        } else if (statusCode == 502) {
            model.addAttribute("errorStatus", "502");
            model.addAttribute("errorMessage", "Bad gateway");
        } else if (statusCode == 503) {
            model.addAttribute("errorStatus", "503");
            model.addAttribute("errorMessage", "Service unavailable");
        } else if (statusCode == 504) {
            model.addAttribute("errorStatus", "504");
            model.addAttribute("errorMessage", "Gateway timeout");
        } else {
            model.addAttribute("errorStatus", "500");
            model.addAttribute("errorMessage", "Something went wrong.");
        }
        return "error";
    }

}
