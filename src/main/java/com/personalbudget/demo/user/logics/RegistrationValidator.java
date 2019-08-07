package com.personalbudget.demo.user.logics;

import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
class RegistrationValidator {

    private UserService userService;

    @Autowired
    public RegistrationValidator(UserService userService) {
        this.userService = userService;
    }   
    
    public Model validateFieldsCompletion(User userForm, Model model) {
        if (userForm.getUsername().isBlank() || userForm.getPassword().isBlank() || userForm.getRepeatPassword().isBlank()) {
            model.addAttribute("message", "All fields must be completed.");
        }
        return model;
    }   
    
    public Model validateUsername(User userForm, Model model) {
        List<User> users = userService.getUsers();
        
        for (User user : users) {
            if (user.getUsername().equals(userForm.getUsername())) {
                model.addAttribute("message", "The username is already in use.");
                return model;
            }
            if (user.getUsername().contains("'") || user.getUsername().contains("\"")) {
                model.addAttribute("message", "Invalid characters.");
                return model;
            }
        }
        
        if ((userForm.getUsername().length() < 4) || (userForm.getUsername().length() > 13)) {
            model.addAttribute("message", "Username must have at least 4 and less than 13 characters.");
        }
        return model;        
    }
    
    public Model validatePassword(User userForm, Model model) {
        if ((userForm.getPassword().length() < 6) || (userForm.getPassword().length() > 32)) {
            model.addAttribute("message", "Password must have at least 6 and less than 32 characters.");
        }        
        if (!userForm.getPassword().equals(userForm.getRepeatPassword())) {
            model.addAttribute("message", "Wrong password confirmation.");
        }
        return model;
    }
}
