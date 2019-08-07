package com.personalbudget.demo.budget;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.budget.entity.Budget;
import com.personalbudget.demo.budget.entity.Expenditure;
import com.personalbudget.demo.budget.logics.BudgetManager;
import com.personalbudget.demo.budget.service.BudgetService;
import com.personalbudget.demo.budget.service.ExpenditureService;
import com.personalbudget.demo.currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private BudgetService budgetService;    
    private CurrencyService currencyService;    
    private ExpenditureService expService;   
    private BudgetManager budgetManager;
    
    @Autowired
    public BudgetController(BudgetService budgetService, CurrencyService currencyService, ExpenditureService expService, BudgetManager budgetManager) {
        this.budgetService = budgetService;
        this.currencyService = currencyService;
        this.expService = expService;
        this.budgetManager = budgetManager;
    }
    
    
    @GetMapping("/show")
    public String showBudget(@RequestParam("id") int id, Model model) {
        if (budgetService.getBudgetById(id).getBudgetName().isEmpty()) {
            return "redirect:/error";        
        }            
        model.addAttribute("budget", budgetService.getBudgetById(id));
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("expenditures", expService.getExpenditures(id)); 
        model.addAttribute("sum", budgetManager.getSumOfAllExpenditures(id));
        return "show-budget";
    }
    
    @GetMapping("/show/delete")
    public String deleteExpenditure(@RequestParam("id") int budgetId, @RequestParam("expId") int expId, @RequestParam("edit") String edit, RedirectAttributes redirectAttributes) {
        redirectAttributes = budgetManager.deleteExpenditure(expId, budgetId, redirectAttributes);        
        if (haveError(redirectAttributes)) {
            return "redirect:/error";        
        }            
        if (!edit.equals("true")) {
            return "redirect:/budgets/show?id=" + budgetId;
        }            
        return "redirect:/budgets/show/edit?id=" + budgetId;
    }
    
    @GetMapping("/show/edit")
    public String editBudget(@RequestParam("id") int id, Model model) {        
        if (budgetService.getBudgetById(id).getBudgetName().isEmpty()) {
            return "redirect:/error";        
        }
        model.addAttribute("budget", budgetService.getBudgetById(id));
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("expenditures", expService.getExpenditures(id));        
        model.addAttribute("sum", budgetManager.getSumOfAllExpenditures(id));        
        return "edit-budget";
    }
    
    @GetMapping("/show/addExpenditure")
    public String newExpenditure(@RequestParam("id") int id, @RequestParam("edit") String edit, Model model) {      
        model.addAttribute("max", budgetService.getBudgetById(id).getAmount() - budgetManager.getSumOfAllExpenditures(id));        
        model.addAttribute("edit", edit);
        model.addAttribute("budgetId", id);
        model.addAttribute("expenditure", new Expenditure(id, edit));
        model.addAttribute("currency", budgetManager.getCurrencyOfBudget(id));
        return "new-expenditure";
    }
    
    @PostMapping("/show/addExpenditure/add")
    public String addExpenditure(@ModelAttribute("expenditure") Expenditure expenditure, RedirectAttributes redirectAttributes) {
        redirectAttributes =  budgetManager.addExpenditure(expenditure, redirectAttributes);        
        if (haveError(redirectAttributes)) {
            return "redirect:/budgets/show/addExpenditure?id=" + expenditure.getBudgetId() + "&edit=" + expenditure.getEdit();        
        }            
        if (expenditure.getEdit().equals("true")) {
            return "redirect:/budgets/show/edit?id=" + expenditure.getBudgetId();                         
        }            
        return "redirect:/budgets/show?id=" + expenditure.getBudgetId();
    }
    
    @GetMapping("/new")
    public String newBudget(Model model) {
        model.addAttribute("budget", new Budget());
        model.addAttribute("currencies", currencyService.getCurrencies());
        return "new-budget";
    }
    
    @PostMapping("/new/add")
    public String addBudget(@ModelAttribute("budget") Budget budget, RedirectAttributes redirectAttributes) {
        redirectAttributes = budgetManager.addBudget(budget, redirectAttributes);
        if (haveError(redirectAttributes)) {
            return "redirect:/budgets/new";
        }            
        return "redirect:/budgets";        
    }
    
    @GetMapping("/delete")
    public String deleteBudget(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {                
        if (budgetService.getBudgetById(id) == null) {
            return "redirect:/error";        
        }            
        budgetService.removeBudget(id);        
        redirectAttributes.addAttribute("success", "removed");
        return "redirect:/budgets";   
    }    
    
    @PostMapping("/update")
    public String updateBudget(@ModelAttribute("budget") Budget budget) {
        budgetManager.updateBudget(budget);
        return "redirect:/budgets/show?id=" + budget.getBudgetId();
    }
}
