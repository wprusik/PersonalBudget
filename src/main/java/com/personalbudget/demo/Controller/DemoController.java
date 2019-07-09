package com.personalbudget.demo.Controller;

import com.personalbudget.demo.Entity.Account;
import com.personalbudget.demo.Entity.Debt;
import com.personalbudget.demo.Entity.Transaction;
import com.personalbudget.demo.Other.TransactionSearch;
import com.personalbudget.demo.Service.AccountService;
import com.personalbudget.demo.Service.BudgetService;
import com.personalbudget.demo.Service.CurrencyService;
import com.personalbudget.demo.Service.DebtService;
import com.personalbudget.demo.Service.ExpenditureCategoryService;
import com.personalbudget.demo.Service.TransactionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.personalbudget.demo.Entity.ExpenditureCategory;
import com.personalbudget.demo.Other.ChartData;
import com.personalbudget.demo.Other.ChartTemplate;
import java.util.LinkedList;
import com.personalbudget.demo.REST.ExchangeManager;
import com.personalbudget.demo.REST.Exchange;

@Controller
public class DemoController {

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ExpenditureCategoryService expCatService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private CurrencyService currencyService;
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private DebtService debtService;
    
    
    
    
    @GetMapping("/accounts")
    public String accountsCard(Model theModel)
    {
        List<Account> accounts = accountService.getAccounts();
        
        theModel.addAttribute("accounts", accounts);
        
        return "accounts";
    }
    
    @GetMapping("/transaction-history")
    public String transactionHistoryCard(Model model) 
    {
        model.addAttribute("transactions", transactionService.getTransactions());   
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("searchTemplate", new TransactionSearch());
        return "transaction-history";
    }
    
    @GetMapping("/planned-transactions")
    public String plannedTransactionsCard(Model model) 
    {
        model.addAttribute("transactions", transactionService.getPlannedTransactions());
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("searchTemplate", new TransactionSearch());
        return "planned-transactions";
    }
    
    @GetMapping("/budgets")
    public String budgetsCard(Model model) 
    {
        Integer integer = 0;
        model.addAttribute("budgets", budgetService.getBudgets());
        model.addAttribute("id", integer);
        return "budgets";
    }
    
    @GetMapping("/debts")
    public String debtsCard(Model model) 
    {
        List<Debt> debts = new ArrayList<Debt>();
        List<Debt> claims = new ArrayList<Debt>();        
        List<Debt> allDebts = debtService.getDebts();
        
        for (Debt item : allDebts)
        {
            if (item.getType().toLowerCase().equals("debt"))
                debts.add(item);
            else if (item.getType().toLowerCase().equals("claim"))
                claims.add(item);
        }
                
        model.addAttribute("debts", debts);
        model.addAttribute("claims", claims);
        
        return "debts";
    }
    
    @GetMapping("/spending-structure")
    public String spendingStructureCard(Model model) 
    {
        
        List<Transaction> transactions = transactionService.getTransactions();
        List<ExpenditureCategory> expCategories = expCatService.getExpenditureCategories();
               
        ExchangeManager exManager = new ExchangeManager();
        
        List<ChartData> data = new ArrayList();
        
                
        for (ExpenditureCategory item : expCategories)
        {
            float totalAmount = 0;
            int numberOfTransactions = 0;
            
            for (Transaction tran : transactions)
            {
                if (tran.getExpenditureType() != null && tran.getType() != null)
                {
                    if (tran.getType().equals("outgoing") && tran.getExpenditureType().equals(item.getExpenditureType()))
                    {
                        numberOfTransactions++;

                        float amount = exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                        totalAmount += amount;
                    }
                }                
            }
            
            ChartData chartData = new ChartData(item.getDescription(), totalAmount, numberOfTransactions, item.getExpenditureType());
            data.add(chartData);
        }
               
        
        if (data.isEmpty())
        {
            ChartData chartData = new ChartData("You don't have any expenditure categories defined", 0, 0, "No expenditure categories");
            data.add(chartData);
        }
        
        
        /*
        int numberOfTransactions = 0;
        float totalAmount = 0;
        
        for (Transaction tran : transactions)
        {
            if (tran.getType() != null)
            {
                if (tran.getType().equals("outgoing") && tran.getExpenditureType() == null)
                {
                    numberOfTransactions++;
                    totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                }
            }            
        }
        
        ChartData chartData = new ChartData("Transactions with undefined type of expenditure", totalAmount, numberOfTransactions, "Undefined");
        data.add(chartData);
        */
        
        model.addAttribute("expCatDistribution", data);
        
        
        List<Account> accounts = accountService.getAccounts();
        List<ChartData> accData = new ArrayList<ChartData>();
        
        if (expCategories != null && accounts != null)
        {
            List<String> tempExpCategories = new LinkedList<>();
            for (ExpenditureCategory tempExpCategory : expCategories)
                tempExpCategories.add(tempExpCategory.getExpenditureType());
            model.addAttribute("expenditureCategories", tempExpCategories);
            
            // tutaj coś jest nie tak - dla każdego typu kategorii wszystkie konta mają przpyisaną tę samą wartość na liście
            for (Account acc : accounts)
            {
                List<Float> tempList = new LinkedList<Float>();
                
                for (ExpenditureCategory expCat : expCategories)
                {
                    float totalAmount = 0;
                    
                    for (Transaction tran : transactions)
                         if (tran.getType() != null && tran.getExpenditureType() != null && tran.getCurrency() != null)
                             if (tran.getType().equals("outgoing") && tran.getExpenditureType().equals(expCat.getExpenditureType()) && tran.getAccountNumberFrom().equals(acc.getAccountNumber()))
                                 totalAmount += exManager.convertCurrency("USD", tran.getCurrency(), tran.getAmount());
                    
                    tempList.add(totalAmount);
                }
                
                                
                String tempString = acc.getBank() + "  " + acc.getAccountName() + " (" + acc.getAccountNumber() + ")";
                ChartData tempData = new ChartData(tempString, tempList);
                accData.add(tempData);
                
            }
            
            model.addAttribute("accData", accData);
        }
        else
        {                       
            if (expCategories == null)    
            {
                List<String> tempList = new ArrayList<String>();
                tempList.add("No expenditure categories defined");
                model.addAttribute("expenditureCategories", tempList);
            }                
            else
            {
                List<String> tempList = new LinkedList<>();
                for (ExpenditureCategory tempExpCategory : expCategories)
                    tempList.add(tempExpCategory.getExpenditureType());
                model.addAttribute("expenditureCategories", tempList);
            }
                
                
            if (accounts == null)
            {
                List<Float> tempList = new LinkedList<>();
                tempList.add((float) 0);
                ChartData tempData = new ChartData("No accounts", tempList);
                model.addAttribute("accData", tempData);
            }
            else
            {                
                for (Account account : accounts)
                {
                    String tempString = account.getBank() + "  " + account.getAccountName() + " (" + account.getAccountNumber() + ")";
                    List<Float> tempList = new LinkedList<>();
                    tempList.add((float) 0);
                    accData.add(new ChartData(tempString, tempList));
                }
                model.addAttribute("accData", accData);
            }
        }        
        
        model.addAttribute("chartTemplate", new ChartTemplate());
        model.addAttribute("accounts", accountService.getAccounts());
        
        return "spending-structure";
    }
    
    
    @GetMapping("/expenditure-categories")
    public String expenditureCategoriesCard(Model theModel) 
    {
        theModel.addAttribute("expenditureCategories", expCatService.getExpenditureCategories());
        
        return "expenditure-categories";
    }
    
    @GetMapping("/exchange-rates")
    public String exchangeRatesCard(Model model) 
    {
        ExchangeManager exManager = new ExchangeManager();        
        Exchange exchange = exManager.getExchanges();        
        model.addAttribute("exchange", exchange);        
        model.addAttribute("currencies", currencyService.getCurrencies());
        return "exchange-rates";
    }
    
    @GetMapping("/about")
    public String acoutCard() 
    {
        return "about";
    }    
}
