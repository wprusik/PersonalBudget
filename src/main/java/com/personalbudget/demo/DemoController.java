package com.personalbudget.demo;

import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.account.service.AccountService;
import com.personalbudget.demo.budget.service.BudgetService;
import static com.personalbudget.demo.currency.exchange.ExchangeManager.getExchanges;
import com.personalbudget.demo.currency.service.CurrencyService;
import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.debt.service.DebtService;
import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.security.SecurityService;
import com.personalbudget.demo.spendingstructure.dto.ChartTemplate;
import com.personalbudget.demo.spendingstructure.logics.SpendingStructureManager;
import com.personalbudget.demo.transaction.dto.TransactionSearch;
import com.personalbudget.demo.transaction.service.TransactionService;
import com.personalbudget.demo.user.entity.User;
import com.personalbudget.demo.user.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    private AccountService accountService;
    private ExpenditureCategoryService expCatService;
    private TransactionService transactionService;
    private CurrencyService currencyService;    
    private BudgetService budgetService;    
    private DebtService debtService;
    private SpendingStructureManager spendingStructureManager;
    private SecurityService securityService;
    private UserService userService;

    @Autowired
    public DemoController(AccountService accountService, ExpenditureCategoryService expCatService, TransactionService transactionService, CurrencyService currencyService, BudgetService budgetService, DebtService debtService, SpendingStructureManager spendingStructureManager, SecurityService securityService, UserService userService) {
        this.accountService = accountService;
        this.expCatService = expCatService;
        this.transactionService = transactionService;
        this.currencyService = currencyService;
        this.budgetService = budgetService;
        this.debtService = debtService;
        this.spendingStructureManager = spendingStructureManager;
        this.securityService = securityService;
        this.userService = userService;
    }
    
    @GetMapping("/accounts")
    public String accountsCard(Model theModel) {
        List<Account> accounts = accountService.getAccounts();        
        theModel.addAttribute("accounts", accounts);        
        return "accounts";
    }
    
    @GetMapping("/transaction-history")
    public String transactionHistoryCard(Model model) {
        model.addAttribute("transactions", transactionService.getTransactions());   
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("searchTemplate", new TransactionSearch());
        return "transaction-history";
    }
    
    @GetMapping("/planned-transactions")
    public String plannedTransactionsCard(Model model) {
        model.addAttribute("transactions", transactionService.getPlannedTransactions());
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("searchTemplate", new TransactionSearch());
        return "planned-transactions";
    }
    
    @GetMapping("/budgets")
    public String budgetsCard(Model model) {
        Integer integer = 0;
        model.addAttribute("budgets", budgetService.getBudgets());
        model.addAttribute("id", integer);
        return "budgets";
    }
    
    @GetMapping("/debts")
    public String debtsCard(Model model) {
        List<Debt> debts = debtService.getOnlyDebts();
        List<Debt> claims = debtService.getOnlyClaims();                
        model.addAttribute("debts", debts);
        model.addAttribute("claims", claims);        
        return "debts";
    }
    
    @GetMapping("/spending-structure")
    public String spendingStructureCard(Model model) {     
        model.addAttribute("expCatDistribution", spendingStructureManager.getExpenditureCategoriesDistribution());
        model.addAttribute("expenditureCategories", spendingStructureManager.getExpenditureCategories());
        model.addAttribute("accData", spendingStructureManager.getAccountsDistribution());       
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("chartTemplate", new ChartTemplate());                
        return "spending-structure";
    }
    
    
    @GetMapping("/expenditure-categories")
    public String expenditureCategoriesCard(Model theModel) {
        theModel.addAttribute("expenditureCategories", expCatService.getExpenditureCategories());        
        return "expenditure-categories";
    }
    
    @GetMapping("/exchange-rates")
    public String exchangeRatesCard(Model model) {     
        model.addAttribute("exchange", getExchanges());        
        model.addAttribute("currencies", currencyService.getCurrencies());
        return "exchange-rates";
    }
    
    @GetMapping("/about")
    public String acoutCard() {
        return "about";
    }    
    
    @GetMapping("/settings")
    public String settingsCard(Model model) {
        User userForm = new User();
        userForm.setEmail(userService.getUser(securityService.getUsernameFromSecurityContext()).getEmail());
        userForm.setUsername(securityService.getUsernameFromSecurityContext());
        model.addAttribute("user", userForm);
        return "settings";
    }
}
