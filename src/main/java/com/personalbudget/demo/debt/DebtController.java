package com.personalbudget.demo.Debt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.personalbudget.demo.currency.service.CurrencyService;
import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.debt.service.DebtService;
import java.util.List;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/debts")
public class DebtController {

    @Autowired
    private DebtService debtService;
    
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping("/delete")
    public String deleteDebt(@RequestParam("id") int id, RedirectAttributes redirectAttributes)
    {
        if (debtService.getDebtById(id) == null)
        {
            redirectAttributes.addFlashAttribute("error", "Something went wrong.");
            return "redirect:/debts";
        }
        
        debtService.deleteDebt(id);
        
        redirectAttributes.addFlashAttribute("success", "The debt has been removed successfully.");
        return "redirect:/debts";
    }
    
    @GetMapping("/new")
    public String newDebt(Model model) {
        
        model.addAttribute("debt", new Debt());
        model.addAttribute("currencies", currencyService.getCurrencies());
        
        return "new-debt";
    }
    
    @PostMapping("/new/add")
    public String addDebt(@ModelAttribute("debt") Debt debt, RedirectAttributes redirectAttributes)
    {        
        if (!debt.getType().equals("debt") && !debt.getType().equals("claim"))
        {
            redirectAttributes.addFlashAttribute("error", "Something went wrong.!");
            return "redirect:/debts/new";
        }
        
        if (debt.getDebtName().trim().isEmpty())
        {
            if (debt.getType().equals("debt"))
                redirectAttributes.addFlashAttribute("error", "Enter the name of the debt.");
            else
                redirectAttributes.addFlashAttribute("error", "Enter the name of the claim.");
            
            return "redirect:/debts/new";
        }
        
        if (debt.getCreditor().trim().isEmpty())
        {
            if (debt.getType().equals("debt"))
                redirectAttributes.addFlashAttribute("error", "Enter the name of the creditor.");
            else
                redirectAttributes.addFlashAttribute("error", "Enter the name of the debtor.");
            
            return "redirect:/debts/new";
        }
        
        debt.setDebtName(debt.getDebtName().trim());
        debt.setCreditor(debt.getCreditor().trim());
        
        List<Debt> debts = debtService.getDebts();
        String debtName = debt.getDebtName();
        boolean alreadyExists = false;
        
        for (Debt item : debts)
            if (item.getDebtName().equals(debtName))
                alreadyExists = true;
        
        if (alreadyExists)
        {
            redirectAttributes.addFlashAttribute("error", "A debt with this name already exists.");
                        
            return "redirect:/debts/new";
        }
        
        
        
        debtService.saveDebt(debt);
        
        redirectAttributes.addFlashAttribute("success", "Debt has been added successfully.");
        return "redirect:/debts";
    }
    
}
