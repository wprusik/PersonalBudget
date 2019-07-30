package com.personalbudget.demo.expenditurecategory;

import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.transaction.service.TransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/expenditure-categories")
public class ExpenditureTypesController {

    @Autowired
    ExpenditureCategoryService expCatService;
    
    @Autowired
    TransactionService transactionService;
    
    @GetMapping("/new")
    public String addCategoryForm(Model model) {
        ExpenditureCategory category = new ExpenditureCategory();
        model.addAttribute("expenditureCategory", category);
        return "new-expenditure-category";
    }
    
    @PostMapping("/new/add")
    public String addCategory(@ModelAttribute("expenditureCategory") ExpenditureCategory expenditureCategory, RedirectAttributes redirectAttributes, Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        expenditureCategory.setUsername(username);
        
        // validate
        // check if there aren't any empty fields
        if (expenditureCategory.getExpenditureType().isBlank() || expenditureCategory.getDescription().isBlank())
        {
            redirectAttributes.addFlashAttribute("error", "All fields must be completed.");
            return "redirect:/expenditure-categories/new";
        }        
        
        
        // validate expenditure type
        if ((expenditureCategory.getExpenditureType().trim().length() < 4) || (expenditureCategory.getExpenditureType().trim().length() > 30))
        {
            redirectAttributes.addFlashAttribute("error", "Expenditure type name must be between 4 and 30 characters long.");
            return "redirect:/expenditure-categories/new";
        }
        else
            expenditureCategory.setExpenditureType(expenditureCategory.getExpenditureType().trim());
        
        
        // check its uniqueness
        if (!expCatService.isUnique(expenditureCategory.getExpenditureType()))
        {
            redirectAttributes.addFlashAttribute("error", "Expenditure type already exists.");
            return "redirect:/expenditure-categories/new";
        }
                
        
        // validate description
        if ((expenditureCategory.getDescription().trim().length() < 1) || (expenditureCategory.getDescription().trim().length() > 255))
        {
            redirectAttributes.addFlashAttribute("error", "Wrong description length.");
            return "redirect:/expenditure-categories/new";
        }
        else 
            expenditureCategory.setDescription(expenditureCategory.getDescription().trim());
        
        expCatService.saveExpenditureCategory(expenditureCategory);
        
        redirectAttributes.addFlashAttribute("success", "added");
        
        return "redirect:/expenditure-categories";
    }
    
    @GetMapping("/delete")
    public String deleteExpenditureType(@RequestParam("expenditureType") String expenditureType, RedirectAttributes redirectAttributes) {        
        
        // affect transactions
        List<Transaction> transactions = transactionService.getTransactions();
        List<Transaction> plannedTransactions = transactionService.getPlannedTransactions();
             
        
        for (Transaction item : transactions)
        {
            if (item.getExpenditureType() != null)
                if (item.getExpenditureType().equals(expenditureType))
                {
                    item.setExpenditureType(null);
                    transactionService.updateTransaction(item);
                }                
        }
        
        for (Transaction item : plannedTransactions)
        {
            if (item.getExpenditureType() != null)
                if (item.getExpenditureType().equals(expenditureType))
                {
                    item.setExpenditureType(null);
                    transactionService.updateTransaction(item);
                }                
        }
        
        expCatService.deleteExpenditureCategory(expenditureType);        
        redirectAttributes.addAttribute("success", "added");        
        return "redirect:/expenditure-categories";
    }
}
