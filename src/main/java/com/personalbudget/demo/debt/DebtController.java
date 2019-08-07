package com.personalbudget.demo.Debt;

import static com.personalbudget.demo.CommonTools.haveError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.personalbudget.demo.currency.service.CurrencyService;
import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.debt.logics.DebtManager;
import com.personalbudget.demo.debt.service.DebtService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/debts")
public class DebtController {

    private DebtService debtService;
    private CurrencyService currencyService;
    private DebtManager debtManager;
    
    @Autowired
    public DebtController(DebtService debtService, CurrencyService currencyService, DebtManager debtManager) {
        this.debtService = debtService;
        this.currencyService = currencyService;
        this.debtManager = debtManager;
    }
    
    @GetMapping("/delete")
    public String deleteDebt(@RequestParam("id") int id, RedirectAttributes redirectAttributes)
    {
        if (debtService.getDebtById(id) == null) {
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
    public String addDebt(@ModelAttribute("debt") Debt debt, RedirectAttributes redirectAttributes) {        
        redirectAttributes = debtManager.addDebt(debt, redirectAttributes);        
        if (haveError(redirectAttributes)) {
            return "redirect:/debts/new";        
        }            
        return "redirect:/debts";
    }
    
}
