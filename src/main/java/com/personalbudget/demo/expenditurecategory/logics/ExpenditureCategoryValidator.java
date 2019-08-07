package com.personalbudget.demo.expenditurecategory.logics;

import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
class ExpenditureCategoryValidator {

    private ExpenditureCategoryService expCatService;
    
    @Autowired
    ExpenditureCategoryValidator(ExpenditureCategoryService expCatService) {
        this.expCatService = expCatService;
    }
    
    boolean areAllFieldsCompleted(ExpenditureCategory expenditureCategory) {
        if (expenditureCategory.getExpenditureType().isBlank() || expenditureCategory.getDescription().isBlank()) {
            return false;
        }    
        return true;
    }
    
    RedirectAttributes validateExpenditureType(ExpenditureCategory expenditureCategory, RedirectAttributes redirectAttributes) {
        if ((expenditureCategory.getExpenditureType().length() < 4) || (expenditureCategory.getExpenditureType().length() > 30)) {
            redirectAttributes.addFlashAttribute("error", "Expenditure type name must be between 4 and 30 characters long.");
            return redirectAttributes;
        }
        if (!expCatService.isUnique(expenditureCategory.getExpenditureType())) {
            redirectAttributes.addFlashAttribute("error", "Expenditure type already exists.");
        }
        return redirectAttributes;
    }
    
    RedirectAttributes validateDescription(ExpenditureCategory expenditureCategory, RedirectAttributes redirectAttributes) {
        if ((expenditureCategory.getDescription().trim().length() < 1) || (expenditureCategory.getDescription().trim().length() > 255)) {
            redirectAttributes.addFlashAttribute("error", "Wrong description length.");            
        }
        return redirectAttributes;
    }
    
}
