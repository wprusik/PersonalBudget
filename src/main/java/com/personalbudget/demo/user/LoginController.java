package com.personalbudget.demo.user;

import static com.personalbudget.demo.CommonTools.haveError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.logics.RegistrationManager;
import com.personalbudget.demo.user.service.UserService;

@Controller
public class LoginController {
        
    private UserService userService;
    private RegistrationManager registrationManager;

    @Autowired
    public LoginController(UserService userService, RegistrationManager registrationManager) {
        this.userService = userService;
        this.registrationManager = registrationManager;
    }
    
    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "login";
    }
    
    @GetMapping("/signup")
    public String registerPage(Model model) {
        model.addAttribute("userForm", new User());        
        return "register";
    }
    
    @PostMapping("/signup")
    public String registration(@ModelAttribute("userForm") User userForm, Model model) {
        model = registrationManager.registerUser(userForm, model);
        if (haveError(model)) {
            return "register";
        }
        return "login";
    }
    
}
