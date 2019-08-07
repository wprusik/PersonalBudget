package com.personalbudget.demo.budget.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.budget.entity.Budget;
import com.personalbudget.demo.budget.entity.Expenditure;
import com.personalbudget.demo.budget.service.BudgetService;
import com.personalbudget.demo.budget.service.ExpenditureService;
import com.personalbudget.demo.currency.entity.Currency;
import com.personalbudget.demo.currency.exchange.ExchangeManager;
import com.personalbudget.demo.currency.service.CurrencyService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class BudgetManager {

    private BudgetService budgetService;
    private ExpenditureService expService;
    private BudgetValidator validator;   
    private CurrencyService currencyService;
    
    @Autowired
    public BudgetManager(BudgetService budgetService, ExpenditureService expService, BudgetValidator validator, CurrencyService currencyService) {
        this.budgetService = budgetService;
        this.expService = expService;
        this.validator = validator;
        this.currencyService = currencyService;
    }
    
    public RedirectAttributes deleteExpenditure(int expId, int budgetId, RedirectAttributes redirectAttributes) {        
        if (!validator.isExpenditureCorrect(expId, budgetId)) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
            return redirectAttributes;
        }
        expService.removeExpenditure(expId);
        redirectAttributes.addFlashAttribute("success", "Expenditure has been removed.");
        return redirectAttributes;
    }
    
    public float getSumOfAllExpenditures(int budgetId) {
        List<Expenditure> expenditures = expService.getExpenditures(budgetId);        
        float sum = 0;
        for (Expenditure item : expenditures)
            sum += item.getAmount();
        return sum;
    }
    
    public Currency getCurrencyOfBudget(int budgetId) {
        List<Currency> currencies = currencyService.getCurrencies();
        Currency currency = new Currency();        
        for (Currency item : currencies) {
            if (item.getCurrency().equals(budgetService.getBudgetById(budgetId).getCurrency())) {
                currency = item;  
            }
        }
        return currency;
    }
    
    public RedirectAttributes addExpenditure(Expenditure expenditure, RedirectAttributes redirectAttributes) {
        expenditure.setDescription(expenditure.getDescription().trim());                
        if (validator.isExpenditureDescriptionCorrect(expenditure.getDescription())) {
            expService.addExpenditure(expenditure);        
            redirectAttributes.addFlashAttribute("success", "New expenditure has been added successfully.");            
        } else {
            redirectAttributes.addFlashAttribute("error", "Enter the description correctly.");
        }
        return redirectAttributes;
    }
    
    public RedirectAttributes addBudget(Budget budget, RedirectAttributes redirectAttributes) {        
        budget.setBudgetName(budget.getBudgetName().trim());
        budget.setPurpose(budget.getPurpose().trim());        
        redirectAttributes = validator.validateBudget(budget, redirectAttributes);
        
        if (!haveError(redirectAttributes)) {
            budgetService.addBudget(budget);
            redirectAttributes.addAttribute("success", "added");
        }
        return redirectAttributes;
    }
    
    public void updateBudget(Budget budget) {
        List<Expenditure> expenditures = expService.getExpenditures(budget.getBudgetId());        
        ExchangeManager converter = new ExchangeManager();        
        String currencyFrom =  budgetService.getBudgetById(budget.getBudgetId()).getCurrency();        
        String currencyTo = budget.getCurrency();
        budgetService.updateBudget(budget);    
        
        if (expenditures != null && expenditures.size() > 0) {
            for (Expenditure item : expenditures) {
                float amountFrom = item.getAmount();
                float temp = converter.convertCurrency(currencyTo, currencyFrom, amountFrom);
                item.setAmount(temp);
            }            
            expService.updateExpenditures(expenditures);
        }
    }    
}
