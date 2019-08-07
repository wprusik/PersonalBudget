package com.personalbudget.demo.budget.logics;

import com.personalbudget.demo.budget.entity.Budget;
import com.personalbudget.demo.budget.service.BudgetService;
import com.personalbudget.demo.budget.service.ExpenditureService;
import com.personalbudget.demo.security.SecurityService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
class BudgetValidator {
    
    private BudgetService budgetService;
    private SecurityService securityService;
    private ExpenditureService expService;
    
    @Autowired
    BudgetValidator(BudgetService budgetService, SecurityService securityService, ExpenditureService expService) {
        this.budgetService = budgetService;
        this.securityService = securityService;
        this.expService = expService;
    }
    
    boolean isExpenditureCorrect(int expId, int budgetId) {
        String username = securityService.getUsernameFromSecurityContext();
        if ((expService.getExpenditureById(expId).getBudgetId() != budgetId) || !(budgetService.getBudgetById(budgetId).getUsername().equals(username))) {
            return false;
        }
        return true;
    }
    
    boolean isBudgetCorrect(int id) {
        if (budgetService.getBudgetById(id).getBudgetName().isEmpty()) {
            return false;
        }
        return true;
    }
    
    boolean isExpenditureDescriptionCorrect(String description) {
        if (description.length() < 4) {
            return false;
        }
        return true;
    }
    
    RedirectAttributes validateBudget(Budget budget, RedirectAttributes redirectAttributes) {
        if (budget.getBudgetName().length() < 4) {
            redirectAttributes.addFlashAttribute("error", "Enter the budget name correctly.");
        }
        if (budget.getPurpose().length() < 4) {
            redirectAttributes.addFlashAttribute("error", "Enter the purpose correctly.");
        }
        
        List<Budget> budgets = budgetService.getBudgets();        
        for (Budget item : budgets) {
            if (item.getBudgetName().equals(budget.getBudgetName())) {
                redirectAttributes.addFlashAttribute("error", "The budget with that name already exists.");
            }
        }
        return redirectAttributes;        
    }
    
    
}
