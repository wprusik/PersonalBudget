package com.personalbudget.demo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


public class CommonToolsTest {

    private static RedirectAttributes redirectAttributes;
    private static Model model;
    
    @BeforeAll
    private static void initialize() {
        redirectAttributes = new RedirectAttributesModelMap();
        model = new ExtendedModelMap();
    }
    
    @Test
    public void haveErrorShouldReturnFalse_RedirectAttributes() {
        assertFalse(CommonTools.haveError(redirectAttributes));
    }
    
    @Test
    public void haveErrorShouldReturnTrue_RedirectAttributes() {
        // given
        redirectAttributes.addAttribute("error", "test");
        // when/then
        assertTrue(CommonTools.haveError(redirectAttributes));
    }
    
    @Test
    public void haveErrorShouldReturnFalse_Model() {
        assertFalse(CommonTools.haveError(model));
    }
    
    @Test
    public void haveErrorShouldReturnTrue_Model() {
        // given
        model.addAttribute("error", "Error ocured");
        // when/then
        assertTrue(CommonTools.haveError(model));
    }
}