package com.personalbudget.demo.user.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.user.entity.Authority;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class RegistrationManager {

    private RegistrationValidator validator;
    private UserService userService;

    @Autowired
    public RegistrationManager(RegistrationValidator validator, UserService userService) {
        this.validator = validator;
        this.userService = userService;
    }
    
    public Model registerUser(User userForm, Model model) {       
        model = validator.validateFieldsCompletion(userForm, model);
        if (haveError(model)) {
            return model;
        }
        
        userForm.setUsername(userForm.getUsername().trim().replaceAll(" +", " "));
        userForm.setPassword(userForm.getPassword().replaceAll(" ", ""));        
        model = validator.validatePassword(userForm, model);
        model = validator.validateUsername(userForm, model);
        
        if (haveError(model)) {
            return model;
        }
        
        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        authority.setUsername(userForm.getUsername());
        ArrayList tempList = new ArrayList();
        tempList.add(authority);
        userService.saveUser(userForm, tempList);
        model.addAttribute("message", "Your account is ready to use. Now you can sign in.");
        return model;
    }
    
}
