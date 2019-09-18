package com.personalbudget.demo.debt.logics;

import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.debt.service.DebtService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
class DebtValidator {
    
    private DebtService debtService;
    
    @Autowired
    DebtValidator(DebtService debtService) {
        this.debtService = debtService;
    }

    protected RedirectAttributes validateDebt(Debt debt, RedirectAttributes redirectAttributes) {
        if (!debt.getType().equals("debt") && !debt.getType().equals("claim")) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong.!");
        }
        
        if (debt.getDebtName().trim().isEmpty()) {
            if (debt.getType().equals("debt"))
                redirectAttributes.addFlashAttribute("error", "Enter the name of the debt.");
            else
                redirectAttributes.addFlashAttribute("error", "Enter the name of the claim.");
        }
        
        if (debt.getCreditor().trim().isEmpty()) {
            if (debt.getType().equals("debt"))
                redirectAttributes.addFlashAttribute("error", "Enter the name of the creditor.");
            else
                redirectAttributes.addFlashAttribute("error", "Enter the name of the debtor.");
        }
        
        List<Debt> debts = debtService.getDebts();
        String debtName = debt.getDebtName().trim();
        boolean alreadyExists = false;        
        for (Debt item : debts)
            if (item.getDebtName().equals(debtName))
                alreadyExists = true;
        
        if (alreadyExists) {
            redirectAttributes.addFlashAttribute("error", "A debt with this name already exists.");
        }
                
        return redirectAttributes;
    }
}
