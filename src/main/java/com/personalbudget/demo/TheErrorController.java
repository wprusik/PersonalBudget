package com.personalbudget.demo;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TheErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "Error 404");
                model.addAttribute("description", "Page not found");
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("error", "Error 500");
                model.addAttribute("description", "Internal server error");
            }
            else if (statusCode == HttpStatus.BAD_GATEWAY.value()) {
                model.addAttribute("error", "Error while fetching exchange rates");
                model.addAttribute("description", "Check the availability of the exchangeratesapi.io website");
            }
            else {
                model.addAttribute("error", HttpStatus.valueOf(statusCode));
            }
        }                
        return "error";
    }
    
    @Override
    public String getErrorPath() {
        return "error";
    }
}
