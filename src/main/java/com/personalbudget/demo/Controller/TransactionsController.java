package com.personalbudget.demo.Controller;

import com.personalbudget.demo.Entity.Account;
import com.personalbudget.demo.Entity.Debt;
import com.personalbudget.demo.Entity.Transaction;
import com.personalbudget.demo.Other.TransactionSearch;
import com.personalbudget.demo.REST.ExchangeManager;
import com.personalbudget.demo.Service.AccountService;
import com.personalbudget.demo.Service.CurrencyService;
import com.personalbudget.demo.Service.DebtService;
import com.personalbudget.demo.Service.ExpenditureCategoryService;
import com.personalbudget.demo.Service.TransactionService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
@RequestMapping("/transactions")
public class TransactionsController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private DebtService debtService;
    
    @Autowired
    private ExpenditureCategoryService expCatService;
    
    @Autowired
    private CurrencyService currencyService;
    
    @GetMapping("/delete")
    public String deleteTransaction(@RequestParam("id") int id, @RequestParam("planned") String ifPlanned, RedirectAttributes redirectAttributes) {
        
        // affect accounts
        ExchangeManager exManager = new ExchangeManager();
        Transaction transaction = transactionService.getTransactionById(id);

        
        if (transaction.getType().equals("incoming"))
        {
            String fromCurrency = transaction.getCurrency();
            String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
            float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());


            Account tempAccount = accountService.getAccount(transaction.getAccountNumberTo());
            float tempBalance = (float) (tempAccount.getBalance() - amount);
            tempAccount.setBalance(tempBalance);
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("outgoing"))
        {
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
            float tempBalance = tempAccount.getBalance() + transaction.getAmount();
            tempAccount.setBalance(tempBalance);
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("between"))
        {
            String fromCurrency = transaction.getCurrency();
            String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
            float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());
            Account tempAccountFrom = accountService.getAccount(transaction.getAccountNumberFrom());
            Account tempAccountTo = accountService.getAccount(transaction.getAccountNumberTo());
            tempAccountFrom.setBalance(tempAccountFrom.getBalance() + transaction.getAmount());
            tempAccountTo.setBalance(tempAccountTo.getBalance() - amount);
            accountService.updateAccount(tempAccountTo);
            accountService.updateAccount(tempAccountFrom);
        }
        else if (transaction.getType().equals("deposit"))
        {
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberTo());
            tempAccount.setBalance(tempAccount.getBalance() - transaction.getAmount());
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("withdraw"))
        {
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
            tempAccount.setBalance(tempAccount.getBalance() + transaction.getAmount());
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("debt"))
        {
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
            tempAccount.setBalance(tempAccount.getBalance() + transaction.getAmount());
            accountService.updateAccount(tempAccount);
            // affect debt
            Debt tempDebt = debtService.getDebtById(transaction.getDebtId());
            String currency = tempDebt.getCurrency();
            float convertedAmount = (float) exManager.convertCurrency(currency, transaction.getCurrency(), transaction.getAmount());
            tempDebt.setAmount(tempDebt.getAmount() + convertedAmount);
            debtService.updateDebt(tempDebt);               
        }
        else
            System.out.println("\n\nSomething went wrong... Unknown type of transaction!\n\n");
                
        
        transactionService.deleteTransaction(id);
        redirectAttributes.addAttribute("success", "delete");
        
        if (ifPlanned.equals("true"))
            return "redirect:/planned-transactions";        
        return "redirect:/transaction-history";
    }
    
    @GetMapping("/newTransaction")
    public String addNewTransactionForm(@RequestParam("planned") String ifPlanned, Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("expenditureTypes", expCatService.getExpenditureCategories());
        model.addAttribute("currencies", currencyService.getCurrencies());    
        model.addAttribute("debts", debtService.getDebts());     
        
        
        if (ifPlanned.equals("true"))
            return "new-planned-transaction";        
        return "new-transaction";
    }
    
    @GetMapping("/newDebtRepayment")
    public String addNewTransactionForm(@RequestParam("id") int debtId, Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("currencies", currencyService.getCurrencies());    
        model.addAttribute("debt", debtService.getDebtById(debtId));
        
                
        return "new-debt-repayment";
    }
    
    
    @PostMapping("/newTransaction/add")
    public String addNewTransaction(@ModelAttribute("transaction") Transaction transaction, RedirectAttributes redirectAttributes)
    {
        
        if (transaction.getIfIsDebtRepayment() == null)
            transaction.setIfIsDebtRepayment("no");
        
        
        if (transaction.getDate() != null)
            transaction.setDateTime(transaction.getDate().atStartOfDay());
        
        // get username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
                               
        transaction.setUsername(username);
        
                

        if (transaction.getCurrency() == null)
            if (!transaction.getType().equals("incoming"))
            {
                if (transaction.getType().equals("deposit"))
                    transaction.setCurrency(accountService.getAccount(transaction.getAccountNumberTo()).getCurrency());
                else
                    transaction.setCurrency(accountService.getAccount(transaction.getAccountNumberFrom()).getCurrency());
            }

        ExchangeManager exManager = new ExchangeManager();


        // affect accounts
        if (transaction.getType().equals("incoming"))
        {
            transaction.setDescription(transaction.getDescription().trim());
            if (transaction.getDescription().isBlank() || transaction.getDescription().length() < 4)
            {
                redirectAttributes.addFlashAttribute("error", "Enter the correct description.");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";
            }            
            
            String fromCurrency = transaction.getCurrency();
            String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
            float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());


            Account tempAccount = accountService.getAccount(transaction.getAccountNumberTo());
            float tempBalance = (float) (tempAccount.getBalance() + amount);
            tempAccount.setBalance(tempBalance);
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("outgoing"))
        {
            if (transaction.getExpenditureType().isBlank())
                transaction.setExpenditureType(null);
            
            transaction.setDescription(transaction.getDescription().trim());
            if (transaction.getDescription().isBlank() || transaction.getDescription().length() < 4)
            {
                redirectAttributes.addFlashAttribute("error", "Enter the correct description.");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";
            }            
            
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
            
            if (tempAccount.getBalance() < transaction.getAmount())
            {
                redirectAttributes.addAttribute("error","lackOfMoney");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";            
            }
            
            float tempBalance = tempAccount.getBalance() - transaction.getAmount();
            tempAccount.setBalance(tempBalance);
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("between"))
        {
            transaction.setDescription(transaction.getDescription().trim());
            if (transaction.getDescription().isBlank() || transaction.getDescription().length() < 4)
            {
                redirectAttributes.addFlashAttribute("error", "Enter the correct description.");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";
            }
            
            String fromCurrency = transaction.getCurrency();
            String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
            float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());
            Account tempAccountFrom = accountService.getAccount(transaction.getAccountNumberFrom());
            Account tempAccountTo = accountService.getAccount(transaction.getAccountNumberTo());
            
            if (tempAccountFrom.getBalance() < transaction.getAmount())
            {
                redirectAttributes.addAttribute("error","lackOfMoney");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";              
            }
            
            tempAccountFrom.setBalance(tempAccountFrom.getBalance() - transaction.getAmount());
            tempAccountTo.setBalance(tempAccountTo.getBalance() + amount);
            accountService.updateAccount(tempAccountTo);
            accountService.updateAccount(tempAccountFrom);
        }
        else if (transaction.getType().equals("deposit"))
        {
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberTo());
            tempAccount.setBalance(tempAccount.getBalance() + transaction.getAmount());
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("withdraw"))
        {
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
            
            if (tempAccount.getBalance() < transaction.getAmount())
            {
                redirectAttributes.addAttribute("error","lackOfMoney");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";              
            }
            
            tempAccount.setBalance(tempAccount.getBalance() - transaction.getAmount());
            accountService.updateAccount(tempAccount);
        }
        else if (transaction.getType().equals("debt"))
        {
            transaction.setDescription(transaction.getDescription().trim());
            if (transaction.getDescription().isBlank() || transaction.getDescription().length() < 4)
            {
                redirectAttributes.addFlashAttribute("error", "Enter the correct description.");                
                if (transaction.getIfIsDebtRepayment().equals("yes"))
                    return "redirect:/transactions/newDebtRepayment?id=" + transaction.getDebtId();
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";
            }
            
            Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
            
            if (tempAccount.getBalance() < transaction.getAmount())
            {
                if (transaction.getIfIsDebtRepayment().equals("yes"))
                {
                    redirectAttributes.addFlashAttribute("error","Transaction has not been added. You don't have that much money on that account.");
                    return "redirect:/transactions/newDebtRepayment?id=" + transaction.getDebtId();
                }
                
                redirectAttributes.addAttribute("error","lackOfMoney");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";             
            }
            
                    
            Debt tempDebt = debtService.getDebtById(transaction.getDebtId());
            
            System.out.println("\n\n" + exManager.convertCurrency(tempDebt.getCurrency(), transaction.getCurrency(), transaction.getAmount()) + "\n\n");
            
            if (tempDebt.getAmount() < (float) exManager.convertCurrency(tempDebt.getCurrency(), transaction.getCurrency(), transaction.getAmount()))
            {                    
                if (transaction.getIfIsDebtRepayment().equals("yes"))
                {
                    redirectAttributes.addFlashAttribute("error","The transaction has not been added. The debt is lower than the amount you tried to pay.");
                    return "redirect:/transactions/newDebtRepayment?id=" + transaction.getDebtId();
                }
                
                redirectAttributes.addAttribute("error","tooMuchMoney");
                if (transaction.getDateTime() != null)
                    return "redirect:/transactions/newTransaction?planned=true";
                else return "redirect:/transactions/newTransaction?planned=false";
            }
            
            
            tempAccount.setBalance(tempAccount.getBalance() - transaction.getAmount());
            accountService.updateAccount(tempAccount);
            // affect debt            
            String currency = tempDebt.getCurrency();
            float convertedAmount = (float) exManager.convertCurrency(currency, transaction.getCurrency(), transaction.getAmount());
            tempDebt.setAmount(tempDebt.getAmount() - convertedAmount);
            debtService.updateDebt(tempDebt);               
        }
        else
            System.out.println("\n\nSomething went wrong... Unknown type of transaction!\n\n");
       
                
        transactionService.saveTransaction(transaction);        
        
        
        if (transaction.getIfIsDebtRepayment().equals("yes"))
        {
            redirectAttributes.addFlashAttribute("success", "Transaction was successfully created.");
            return "redirect:/debts";
        }
        
        redirectAttributes.addAttribute("success", "added");     
        
        if (transaction.getDateTime() != null)
            return "redirect:/planned-transactions";
        return "redirect:/transaction-history";
    }   
    
    @PostMapping("/search")
    public String searchTransactionHistory(@ModelAttribute("searchTemplate") TransactionSearch searchTemplate, Model model, RedirectAttributes redirectAttributes)
    {        
        List<Transaction> transactions;
        List<Transaction> finalList = new ArrayList<Transaction>();
        boolean isPlanned;
        if (searchTemplate.getIsPlanned().equals("yes"))
        {
            isPlanned = true;
            transactions = transactionService.getPlannedTransactions();
        }            
        else
        {
            isPlanned = false;
            transactions = transactionService.getTransactions();
        }
        
        
        if (transactions == null)
        {
            redirectAttributes.addAttribute("error", "emptyList");
            if (isPlanned)
                return "redirect:/planned-transactions";
            return "redirect:/transaction-history";
        }
        
        if (searchTemplate.getDescription().equals("") && searchTemplate.getFromAccount().equals("") && searchTemplate.getToAccount().equals("") && searchTemplate.getCurrency().equals("") && searchTemplate.getStartDate() == null & searchTemplate.getEndDate() == null)
        {
            redirectAttributes.addAttribute("error", "searchNull");
            if (isPlanned)
                return "redirect:/planned-transactions";
            return "redirect:/transaction-history";
        }       
        
        
        for (Transaction item : transactions)
        {
            boolean tempBool = true;            
            if (!searchTemplate.getDescription().equals("") && !searchTemplate.getDescription().trim().equals(item.getDescription()))
                tempBool = false;            
            if (!searchTemplate.getFromAccount().equals("") && !searchTemplate.getFromAccount().equals(item.getAccountNumberFrom()))
                tempBool = false;
            if (!searchTemplate.getToAccount().equals("") && searchTemplate.getToAccount() != null && !searchTemplate.getToAccount().equals(item.getAccountNumberTo()))
                tempBool = false;
            if (!searchTemplate.getCurrency().equals("") && !searchTemplate.getCurrency().equals(item.getCurrency()))
                tempBool = false;
            if (searchTemplate.getStartDate() != null)
            {
                LocalDateTime startLdf = LocalDateTime.ofInstant(searchTemplate.getStartDate().toInstant(), ZoneId.systemDefault());
                if (searchTemplate.getEndDate() != null)
                {
                    LocalDateTime endLdf = LocalDateTime.ofInstant(searchTemplate.getEndDate().toInstant(), ZoneId.systemDefault());
                    if (item.getDateTime().isBefore(startLdf) || item.getDateTime().isAfter(endLdf))
                        tempBool = false;
                }
                else
                {
                    if (item.getDateTime().isBefore(startLdf))
                        tempBool = false;
                }        
            }
            else if (searchTemplate.getEndDate() != null)
            {
                LocalDateTime endLdf = LocalDateTime.ofInstant(searchTemplate.getEndDate().toInstant(), ZoneId.systemDefault());
                if (item.getDateTime().isAfter(endLdf))
                    tempBool = false;
            }
           
            if (tempBool)
                finalList.add(item);
        }
        
        if (finalList.isEmpty())
        {
            redirectAttributes.addAttribute("error", "noTransactions");
            if (isPlanned)
                return "redirect:/planned-transactions";
            return "redirect:/transaction-history";
        }
        
        
         
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("currencies", currencyService.getCurrencies());
        model.addAttribute("searchTemplate", new TransactionSearch());        
        model.addAttribute("transactions", finalList);
        model.addAttribute("message", "Found something that match your criteria.");
        
        if (isPlanned)
             return "planned-transactions";
        return "transaction-history";
    }
    
}
