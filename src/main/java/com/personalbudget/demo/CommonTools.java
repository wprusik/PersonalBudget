package com.personalbudget.demo;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class CommonTools {

    public static boolean haveError(RedirectAttributes redirectAttributes) {
        if (redirectAttributes.containsAttribute("error") || redirectAttributes.getFlashAttributes().containsKey("error")) {
            return true;
        }
        return false;
    }
    
    public static boolean haveError(Model model) {
        if (model.containsAttribute("error")) {
            return true;
        }
        return false;
    }
}
