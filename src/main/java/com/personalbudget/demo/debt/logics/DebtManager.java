package com.personalbudget.demo.debt.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.debt.service.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class DebtManager {

    private DebtService debtService;
    private DebtValidator validator;
    
    @Autowired
    public DebtManager(DebtService debtService, DebtValidator validator) {
        this.debtService = debtService;        
        this.validator = validator;
    }
    
    public RedirectAttributes addDebt(Debt debt, RedirectAttributes redirectAttributes) {
        redirectAttributes = validator.validateDebt(debt, redirectAttributes);         
        
        if (haveError(redirectAttributes))
            return redirectAttributes;   
        
        debt.setDebtName(debt.getDebtName().trim());
        debt.setCreditor(debt.getCreditor().trim());
        debtService.saveDebt(debt);
        redirectAttributes.addFlashAttribute("success", "Debt has been added successfully.");
        return redirectAttributes;
    }
}
