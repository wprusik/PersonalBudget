package com.personalbudget.demo.expenditurecategory.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.security.SecurityService;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.transaction.service.TransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class ExpenditureCategoryManager {

    private SecurityService securityService;
    private ExpenditureCategoryService expCatService;
    private ExpenditureCategoryValidator validator;
    private TransactionService transactionService;
    
    @Autowired
    public ExpenditureCategoryManager(SecurityService securityService, ExpenditureCategoryService expCatService, ExpenditureCategoryValidator validator, TransactionService transactionService) {
        this.securityService = securityService;
        this.expCatService = expCatService;
        this.validator = validator;
        this.transactionService = transactionService;
    }    
    
    public RedirectAttributes addCategory(ExpenditureCategory expenditureCategory, RedirectAttributes redirectAttributes) {        
        String username = securityService.getUsernameFromSecurityContext();
        expenditureCategory.setUsername(username);
        expenditureCategory.setExpenditureType(expenditureCategory.getExpenditureType().trim());
        expenditureCategory.setDescription(expenditureCategory.getDescription().trim());
        
        if(!validator.areAllFieldsCompleted(expenditureCategory)) {
            redirectAttributes.addFlashAttribute("error", "All fields must be completed.");
            return redirectAttributes;
        }
        redirectAttributes = validator.validateExpenditureType(expenditureCategory, redirectAttributes);
        redirectAttributes = validator.validateDescription(expenditureCategory, redirectAttributes);
        
        if (haveError(redirectAttributes)) {
            return redirectAttributes;
        }        
        
        expCatService.saveExpenditureCategory(expenditureCategory);
        redirectAttributes.addFlashAttribute("success", "added");
        return redirectAttributes;
    }
    
    public RedirectAttributes deleteExpenditureType(String expenditureType, RedirectAttributes redirectAttributes) {        
        List<Transaction> transactions = transactionService.getTransactions();
        List<Transaction> plannedTransactions = transactionService.getPlannedTransactions();             
        
        for (Transaction item : transactions) {
            if (item.getExpenditureType() != null)
                if (item.getExpenditureType().equals(expenditureType)) {
                    item.setExpenditureType(null);
                    transactionService.updateTransaction(item);
                }                
        }
        
        for (Transaction item : plannedTransactions) {
            if (item.getExpenditureType() != null)
                if (item.getExpenditureType().equals(expenditureType)) {
                    item.setExpenditureType(null);
                    transactionService.updateTransaction(item);
                }                
        }
        
        expCatService.deleteExpenditureCategory(expenditureType);        
        redirectAttributes.addAttribute("success", "delete");
        return redirectAttributes;
    }    
}
