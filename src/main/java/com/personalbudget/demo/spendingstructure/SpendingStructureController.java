package com.personalbudget.demo.spendingstructure;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.spendingstructure.dto.ChartTemplate;
import com.personalbudget.demo.transaction.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.account.service.AccountService;
import com.personalbudget.demo.spendingstructure.logics.SpendingStructureManager;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/spending-structure")
public class SpendingStructureController {

    private TransactionService transactionService;
    private AccountService accountService;
    private ExpenditureCategoryService expService;
    private SpendingStructureManager spendingStructureManager;

    @Autowired
    public SpendingStructureController(TransactionService transactionService, AccountService accountService, ExpenditureCategoryService expService, SpendingStructureManager spendingStructureManager) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.expService = expService;
        this.spendingStructureManager = spendingStructureManager;
    }
    
    @PostMapping("/custom-chart")
    public String generateCustomChart(Model model, @ModelAttribute("chartTemplate") ChartTemplate template, RedirectAttributes redirectAttributes) {        
        Map data = spendingStructureManager.getCustomChartData(model, redirectAttributes, template);        
        redirectAttributes = (RedirectAttributes)data.get("redirectAttributes");
        if (haveError(redirectAttributes)) {
            return "redirect:/spending-structure";
        }        
        model = (Model)data.get("model");
        return "custom-chart";
    }
}
