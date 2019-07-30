package com.personalbudget.demo.budget;

import com.personalbudget.demo.budget.entity.Budget;
import com.personalbudget.demo.budget.entity.Expenditure;
import com.personalbudget.demo.budget.service.BudgetService;
import com.personalbudget.demo.budget.service.ExpenditureService;
import com.personalbudget.demo.currency.entity.Currency;
import com.personalbudget.demo.currency.exchange.ExchangeManager;
import com.personalbudget.demo.currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    BudgetService budgetService;
    
    @Autowired
    CurrencyService currencyService;
    
    @Autowired
    ExpenditureService expService;
    
    @GetMapping("/show")
    public String showBudget(@RequestParam("id") int id, Model model)
    {
        if (budgetService.getBudgetById(id).getBudgetName().isEmpty())
            return "redirect:/error";
        
        model.addAttribute("budget", budgetService.getBudgetById(id));
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("expenditures", expService.getExpenditures(id));
        
        List<Expenditure> expenditures = expService.getExpenditures(id);
        
        float sum = 0;
        for (Expenditure item : expenditures)
            sum += item.getAmount();
        
        model.addAttribute("sum", sum);
        
        return "show-budget";
    }
    
    @GetMapping("/show/delete")
    public String deleteExpenditure(@RequestParam("id") int budgetId, @RequestParam("expId") int expId, @RequestParam("edit") String edit, RedirectAttributes redirectAttributes)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();  
        if ((expService.getExpenditureById(expId).getBudgetId() != budgetId) || !(budgetService.getBudgetById(budgetId).getUsername().equals(username)))
            return "redirect:/error";
        
        expService.removeExpenditure(expId);
        redirectAttributes.addFlashAttribute("success", "Expenditure has been removed.");
        if (!edit.equals("true"))
            return "redirect:/budgets/show?id=" + budgetId;
        return "redirect:/budgets/show/edit?id=" + budgetId;
    }
    
    @GetMapping("/show/edit")
    public String editBudget(@RequestParam("id") int id, Model model, RedirectAttributes redirectAttributes)
    {        
        if (budgetService.getBudgetById(id).getBudgetName().isEmpty())
            return "redirect:/error";
        
        model.addAttribute("budget", budgetService.getBudgetById(id));
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("expenditures", expService.getExpenditures(id));
        
        List<Expenditure> expenditures = expService.getExpenditures(id);
        
        float sum = 0;
        for (Expenditure item : expenditures)
            sum += item.getAmount();
        
        model.addAttribute("sum", sum); 
       return "edit-budget";
    }
    
    @GetMapping("/show/addExpenditure")
    public String newExpenditure(@RequestParam("id") int id, @RequestParam("edit") String edit, Model model)
    {
        List<Currency> currencies = currencyService.getCurrencies();
        Currency currency = new Currency();
        
        for (Currency item : currencies)
            if (item.getCurrency().equals(budgetService.getBudgetById(id).getCurrency()))
                currency = item;
          
        
        Expenditure expenditure = new Expenditure();
        expenditure.setBudgetId(id);
        expenditure.setEdit(edit);
        
        
        List<Expenditure> expenditures = expService.getExpenditures(id);
        
        float sum = 0;
        for (Expenditure item : expenditures)
            sum += item.getAmount();
        
        
        float funds = budgetService.getBudgetById(id).getAmount() - sum;
        
        model.addAttribute("max", funds);
        
        model.addAttribute("edit", edit);
        model.addAttribute("budgetId", id);
        model.addAttribute("expenditure", expenditure);
        model.addAttribute("currency", currency);
        return "new-expenditure";
    }
    
    @PostMapping("/show/addExpenditure/add")
    public String addExpenditure(@ModelAttribute("expenditure") Expenditure expenditure, RedirectAttributes redirectAttributes)
    { 
        expenditure.setDescription(expenditure.getDescription().trim());
        
        // validate
        if (expenditure.getDescription().length() < 4)
        {
            redirectAttributes.addFlashAttribute("error", "Enter the description correctly.");
            return "redirect:/budgets/show/addExpenditure?id=" + expenditure.getBudgetId() + "&edit=" + expenditure.getEdit();
        }        
        
        expService.addExpenditure(expenditure);
        
        System.out.println("\n\n");
        System.out.println("Expenditure id: " +  expenditure.getBudgetId());;
        System.out.println("\n\n");
        
        if (!expenditure.getEdit().equals("true"))
        {
            redirectAttributes.addFlashAttribute("success", "New expenditure has been added successfully.");
            return "redirect:/budgets/show?id=" + expenditure.getBudgetId();
        }
        
        redirectAttributes.addFlashAttribute("success", "New expenditure has been added successfully.");
        return "redirect:/budgets/show/edit?id=" + expenditure.getBudgetId();
    }
    
    @GetMapping("/new")
    public String newBudget(Model model)
    {
        model.addAttribute("budget", new Budget());
        model.addAttribute("currencies", currencyService.getCurrencies());
        return "new-budget";
    }
    
    @PostMapping("/new/add")
    public String addBudget(@ModelAttribute("budget") Budget budget, RedirectAttributes redirectAttributes)
    {
        budget.setBudgetName(budget.getBudgetName().trim());
        budget.setPurpose(budget.getPurpose().trim());
        
        if (budget.getBudgetName().length() < 4)
        {
            redirectAttributes.addFlashAttribute("error", "Enter the budget name correctly.");
            return "redirect:/budgets/new";
        }
        
        if (budget.getPurpose().length() < 4)
        {
            redirectAttributes.addFlashAttribute("error", "Enter the purpose correctly.");
            return "redirect:/budgets/new";
        }
        
        
        List<Budget> budgets = budgetService.getBudgets();
        
        for (Budget item : budgets)
        {
            if (item.getBudgetName().equals(budget.getBudgetName()))
            {
                redirectAttributes.addFlashAttribute("error", "The budget with that name already exists.");
                return "redirect:/budgets/new";
            }
        }
        
        budgetService.addBudget(budget);
        redirectAttributes.addAttribute("success", "added");
        return "redirect:/budgets";
    }
    
    @GetMapping("/delete")
    public String deleteBudget(@RequestParam("id") int id, RedirectAttributes redirectAttributes)
    {                
        if (budgetService.getBudgetById(id) == null)
            return "redirect:/error";
        
        budgetService.removeBudget(id);        
        redirectAttributes.addAttribute("success", "removed");
        return "redirect:/budgets";   
    }    
    
    @PostMapping("/update")
    public String updateBudget(@ModelAttribute("budget") Budget budget)
    {
        List<Expenditure> expenditures = expService.getExpenditures(budget.getBudgetId());
        
        ExchangeManager converter = new ExchangeManager();        
        String currencyFrom =  budgetService.getBudgetById(budget.getBudgetId()).getCurrency();        
        String currencyTo = budget.getCurrency();
        budgetService.updateBudget(budget);
        
        if (expenditures != null && expenditures.size() > 0)
        {
            for (Expenditure item : expenditures)
            {
                float amountFrom = item.getAmount();
                float temp = converter.convertCurrency(currencyTo, currencyFrom, amountFrom);
                item.setAmount(temp);
            }
            
            expService.updateExpenditures(expenditures);
        }
        
        
        return "redirect:/budgets/show?id=" + budget.getBudgetId();
    }
}
