package com.personalbudget.demo.user.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import static com.personalbudget.demo.DemoApplication.BASEURL;
import com.personalbudget.demo.security.SecurityService;
import com.personalbudget.demo.user.entity.Authority;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.entity.UserActivation;
import com.personalbudget.demo.user.service.UserService;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class RegistrationManager {

    private RegistrationValidator validator;
    private UserService userService;
    private SecurityService securityService;
    
    private static String FROM_EMAIL = "noreply.PersonalBudget@gmail.com";
            
    private static String ACTIVATION_LINK = "activationLink";
    private static String RESET_PASSWORD = "resetPassword";

    @Autowired
    public RegistrationManager(RegistrationValidator validator, UserService userService, SecurityService securityService) {
        this.validator = validator;
        this.userService = userService;
        this.securityService = securityService;
    }
    
    public Model registerUser(User userForm, Model model) {       
        model = validator.validateFieldsCompletion(userForm, model);
        if (haveError(model)) {
            return model;
        }
        
        userForm.setUsername(userForm.getUsername().trim().replaceAll(" +", " "));
        userForm.setPassword(userForm.getPassword().replaceAll(" ", ""));
        userForm.setEmail(userForm.getEmail().replaceAll(" ", ""));
        model = validator.validatePassword(userForm, model);
        model = validator.validateUsername(userForm, model);
        model = validator.validateEmail(userForm, model);
        
        if (haveError(model)) {
            return model;
        }
        
        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        authority.setUsername(userForm.getUsername());
        ArrayList tempList = new ArrayList();
        tempList.add(authority);
        userService.saveUser(userForm, tempList);
        model = sendActivationEmail(userForm.getUsername(), model);        
        return model;
    } 
    
    private Model sendActivationEmail(String username, Model model) {        
        User user = userService.getUser(username);    
        String to = user.getEmail();
        
        try {
            sendEmail(to, "Activation link - Personal Budget", getMessageText(username, ACTIVATION_LINK));
            model.addAttribute("message", "The activation link has been sent to your e-mail address. Check your inbox and click the link to activate the account.");
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error sending activation link. Please try resend it in a moment.");
        }
        
        return model;
    }
    
    private Authenticator getAuthenticator(String from, String password) {
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        };
    }     
    
    public Model resendActivationEmail(String username, Model model) {
        UserActivation activation = userService.getActivation(username);        
        if (activation == null) {
            model.addAttribute("error", "Wrong username");
            return model;
        }        
        userService.updateActivation(activation);
        return sendActivationEmail(username, model);
    }
          
    public Model resetPassword(String username, String email, Model model) {
        User user = userService.getUser(username);
        email = email.trim();
        if (user == null || !email.equals(user.getEmail())) {
            model.addAttribute("error", "Wrong username or e-mail address.");
            return model;
        }
        
        String newPassword = RandomStringUtils.random(15,true,true);        
        userService.updatePassword(username, newPassword);
                      
        try {
            sendEmail(email, "Recover your password to Personal Budget web app", getMessageText(newPassword, RESET_PASSWORD));
            model.addAttribute("message", "The new password has been sent to your e-mail address.");
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error sending e-mail. Please try again in a moment.");
        }
        
        return model;
    }
    
    public RedirectAttributes removeAccount(User userForm, RedirectAttributes redirectAttributes) {
        String username = securityService.getUsernameFromSecurityContext();        
        if (!doesPasswordsMatch(userForm.getPassword(), userService.getUser(username).getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Wrong password entered.");
            return redirectAttributes;
        }
        userService.deleteUser(username);
        redirectAttributes.addFlashAttribute("message", "Your account has been successfully removed.");        
        return redirectAttributes;
    }
    
    public RedirectAttributes updateAccount(User userForm, RedirectAttributes redirectAttributes) {        
        User user = userService.getUser(securityService.getUsernameFromSecurityContext());                                 
        if (!doesPasswordsMatch(userForm.getPassword(), user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Wrong password entered.");
            return redirectAttributes;
        }        
        redirectAttributes = validator.validateAccountUpdate(userForm, user, redirectAttributes);
        
        if (!haveError(redirectAttributes)) {
            if (userForm.getEmail() != null) {
                userService.updateEmail(userForm.getUsername(), userForm.getEmail());
            }
            if (userForm.getNewPassword() != null) {
                userService.updatePassword(userForm.getUsername(), userForm.getNewPassword().trim());
            }            
            redirectAttributes.addFlashAttribute("message", "Account updated successfully.");
        }
        return redirectAttributes;
    }
    
    private boolean doesPasswordsMatch(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    private void sendEmail(String to, String subject, String messageText) throws Exception {
        Session session = getEmailSession();              
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
        message.setSubject(subject);
        message.setText(messageText);
        Transport.send(message);
    }
    
    private String getMessageText(String param, String messageType) {
        String messageText = "";
        
        switch(messageType) {
            case "activationLink": {
                String url = BASEURL + "activate?user=" + param + "&code=" + userService.getActivationCode(param);
                messageText = "There is only one step left to register your account on Personal Budget app. Please enter the link below to activate your account:\n\n"
                    + url
                    + "\n\nThe link will be active for 24 hours. The account will be removed if you won't click it."
                    + "\nIf you haven't created an account in our application, just ignore this message."
                    + "\n\n---\nCheers!\nPersonalBudget team";
                break;
            }
            case "resetPassword": {
                messageText = "Your password has been reset. Your new password is:\n\n"
                        + param
                        + "\n\nSign in to PersonalBudget, go to Others > Settings and set a new password for your account."
                        + "\n\n---\nCheers!\nPersonalBudget team";
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid message type: '" + messageType + "'");
            }
        }
        
        return messageText;
    }
    
    private Session getEmailSession() {
        String from = "noreply.personalbudget";
        String host = "smtp.gmail.com";
        String port = "587";
        String pass = "Pb2020money";
        
        Properties properties = System.getProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        
        Session session = Session.getDefaultInstance(properties, getAuthenticator(from + "@gmail.com", pass));        
        return session;
    }
}
