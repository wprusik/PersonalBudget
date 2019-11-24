package com.personalbudget.demo;

import java.util.Map;
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
    
    public static RedirectAttributes rewriteAttributes(Model from, RedirectAttributes to) {
        Map<String, Object> attributes = from.asMap();        
        for (String key : attributes.keySet()) {
            to.addFlashAttribute(key, attributes.get(key));
        }
        return to;
    }
    
    public static Model rewriteAttributes(RedirectAttributes from, Model to) {
        Map<String, Object> attributes = from.asMap();
        for (String key : attributes.keySet()) {
            to.addAttribute(key, attributes.get(key));
        }
        return to;
    }
}
