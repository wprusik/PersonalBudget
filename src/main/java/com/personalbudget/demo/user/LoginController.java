package com.personalbudget.demo.user;

import static com.personalbudget.demo.CommonTools.haveError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.entity.UserActivation;
import com.personalbudget.demo.user.logics.RegistrationManager;
import com.personalbudget.demo.user.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

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
    
    @GetMapping("/activate")
    public String userActivation(@RequestParam("user") String username, @RequestParam("code") String code, Model model) {
        UserActivation activation = userService.getActivation(username);
        if (activation == null || !activation.getActivationCode().equals(code)) {            
            model.addAttribute("error", "Wrong activation link.");
        }
        else {
            userService.enableUser(username);
            model.addAttribute("message", "Account activated successfully. Now you can sign in.");
        }
        return "login";
    }
    
    @PostMapping("/resendActivationLink")
    public String resendActivationLink(@ModelAttribute("username") String username, Model model) {
        model = registrationManager.resendActivationEmail(username, model);
        return "login";
    }
    
    @PostMapping("/resetPassword")
    public String resetPassword(@ModelAttribute("username") String username, @ModelAttribute("email") String email, Model model) {
        model = registrationManager.resetPassword(username, email, model);       
        return "login";
    }
}
