package com.personalbudget.demo.Controller;

import com.personalbudget.demo.Entity.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.personalbudget.demo.Entity.User;
import com.personalbudget.demo.Service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    
    @Autowired
    UserService userService;
    
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
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        List<User> users = userService.getUsers();
        
        // check if there aren't any empty fields
        if (userForm.getUsername().isBlank() || userForm.getPassword().isBlank() || userForm.getRepeatPassword().isBlank())
        {
            model.addAttribute("message", "All fields must be completed.");
            return "register";
        }        
        
        userForm.setUsername(userForm.getUsername().trim());
        userForm.setPassword(userForm.getPassword().trim());
        
        // validate username        
        for (User user : users) {
            if (user.getUsername().equals(userForm.getUsername()))
            {
                model.addAttribute("message", "The username is already in use.");
                return "register";
            }
            if (user.getUsername().contains("'") || user.getUsername().contains("\""))
            {
                model.addAttribute("message", "Invalid characters.");
                return "register";
            }
        }
        
        // validate username length
        if ((userForm.getUsername().length() < 4) || (userForm.getUsername().length() > 13))
        {
            model.addAttribute("message", "Username must have at least 4 and less than 13 characters.");
            return "register";
        }
        
        // validate password
        if ((userForm.getPassword().length() < 6) || (userForm.getPassword().length() > 32))
        {
            model.addAttribute("message", "Password must have at least 6 and less than 32 characters.");
            return "register";
        }
        
        // validate password confirmation
        if (!userForm.getPassword().equals(userForm.getRepeatPassword()))
        {
            model.addAttribute("message", "Wrong password confirmation.");
            return "register";
        }
        
        
        // add default role
        Authority theAuthority = new Authority();
        theAuthority.setAuthority("ROLE_USER");
        theAuthority.setUsername(userForm.getUsername());
        ArrayList tempList = new ArrayList();
        tempList.add(theAuthority);
        
        System.out.println("\n\n\nUSERNAME: " + userForm.getUsername());
        System.out.println("ROLE: " + theAuthority.getAuthority() + "\n\n\n");
        
        
        userService.saveUser(userForm, tempList);
        model.addAttribute("message", "Your account is ready to use. Now you can sign in.");
        return "login";
    }  
    
}
