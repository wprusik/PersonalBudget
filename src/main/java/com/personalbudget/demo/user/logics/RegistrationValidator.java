package com.personalbudget.demo.user.logics;

import static com.personalbudget.demo.CommonTools.rewriteAttributes;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
class RegistrationValidator {

    private UserService userService;

    @Autowired
    public RegistrationValidator(UserService userService) {
        this.userService = userService;
    }   
    
    public Model validateFieldsCompletion(User userForm, Model model) {
        if (userForm.getUsername().isBlank() || userForm.getPassword().isBlank() || userForm.getRepeatPassword().isBlank()) {
            model.addAttribute("error", "All fields must be completed.");
        }
        return model;
    }   
    
    Model validateUsername(User userForm, Model model) {
        List<User> users = userService.getUsers();
        
        for (User user : users) {
            if (user.getUsername().equals(userForm.getUsername())) {
                model.addAttribute("error", "The username is already in use.");
                return model;
            }
            if (user.getUsername().contains("'") || user.getUsername().contains("\"")) {
                model.addAttribute("error", "Invalid characters.");
                return model;
            }
        }
        
        if ((userForm.getUsername().length() < 4) || (userForm.getUsername().length() > 13)) {
            model.addAttribute("error", "Username must have at least 4 and less than 13 characters.");
        }
        return model;        
    }
    
    Model validatePassword(User userForm, Model model) {
        if ((userForm.getPassword().length() < 6) || (userForm.getPassword().length() > 32)) {
            model.addAttribute("error", "Password must have at least 6 and less than 32 characters.");
        }        
        if (!areEqual(userForm.getPassword(), userForm.getRepeatPassword())) {
            model.addAttribute("error", "Wrong password confirmation.");
        }
        return model;
    }   
    
    Model validateEmail(User userForm, Model model) {
        if ((userForm.getEmail().length() < 10) || (userForm.getEmail().length() > 60)) {
            model.addAttribute("error", "E-mail address must have at least 10 and less than 60 characters.");
        }
        if (!isEmailAddressFormatCorrect(userForm.getEmail())) {
            model.addAttribute("error", "Invalid e-mail address format.");
        }
        if (!isEmailAddressAvailable(userForm.getEmail())) {
            model.addAttribute("error", "The e-mail address is already in use.");
        }
        return model;
    }
    
    RedirectAttributes validateAccountUpdate(User userForm, User currentUser, RedirectAttributes redirectAttributes) {
        if (!areEqual(userForm.getEmail(), currentUser.getEmail())) {
            redirectAttributes = validateEmail(userForm, redirectAttributes);
        }
        if (userForm.getNewPassword() != null || userForm.getRepeatPassword() != null) {
            redirectAttributes = validatePassword(userForm.getNewPassword(), userForm.getRepeatPassword(), redirectAttributes);
        }
        return redirectAttributes;
    }
    
    boolean areEqual(String password1, String password2) {
        if (password1.equals(password2)) {
            return true;
        }
        return false;
    }
    
    private RedirectAttributes validateEmail(User userForm, RedirectAttributes redirectAttributes) {
        Model model = new ExtendedModelMap();
        model = validateEmail(userForm, model);
        return rewriteAttributes(model, redirectAttributes);
    }
    
    private RedirectAttributes validatePassword(String password, String passwordConfirmation, RedirectAttributes redirectAttributes) {
        Model model = new ExtendedModelMap();
        model = validatePassword(password, passwordConfirmation, model);
        return rewriteAttributes(model, redirectAttributes);
    }
    
    private Model validatePassword(String password, String passwordConfirmation, Model model) {                
        if ((password.trim().length() < 6) || (password.trim().length() > 32)) {
            model.addAttribute("error", "Password must have at least 6 and less than 32 characters.");
        }
        else if (!areEqual(password, passwordConfirmation)) {
            model.addAttribute("error", "Entered passwords does not match.");
        }
        return model;
    }
    
    private boolean isEmailAddressFormatCorrect(String emailAddress) {
        if (doesEmailAddressContainAnyUnallowedCharacters(emailAddress)) {
            return false;
        }        
        String[] emailAddressParts = emailAddress.split("@");
        if (emailAddressParts.length != 2 || emailAddressParts[0].length() < 3 || emailAddressParts[0].length() > 54) {
            return false;
        }
        return isEmailDomainFormatCorrect(emailAddressParts[1]);
    }
    
    private boolean isEmailDomainFormatCorrect(String domain) {
        String[] domainParts = domain.split("\\.");
        if (domainParts.length < 2) {
            return false;
        }
        if (domain.length() < 4) {
            return false;
        }
        return true;
    }
    
    private boolean doesEmailAddressContainAnyUnallowedCharacters(String emailAddress) {
        String[] unallowedCharacters = {"..", "ą", "ć", "ę", "ł", "ń", "ó", "ś", "ź", "ż", "(", ")", "/", "\\", "[", "]", ",", "[", "]", ";", ":", "*"};
        return Arrays.stream(unallowedCharacters).parallel().anyMatch(emailAddress::contains);
    }
    
    private boolean isEmailAddressAvailable(String emailAddress) {
        List<User> users = userService.getUsers();
        
        for (User user : users) {
            if (user.getEmail().equals(emailAddress)) {
                return false;
            }
        }
        return true;
    }
}
