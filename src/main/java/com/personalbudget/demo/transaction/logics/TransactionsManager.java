package com.personalbudget.demo.transaction.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.account.service.AccountService;
import com.personalbudget.demo.currency.exchange.ExchangeManager;
import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.debt.service.DebtService;
import com.personalbudget.demo.expenditurecategory.service.ExpenditureCategoryService;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.transaction.service.TransactionService;
import com.personalbudget.demo.transaction.dto.TransactionSearch;
import com.personalbudget.demo.security.SecurityService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class TransactionsManager {

    private TransactionService transactionService;
    private AccountService accountService;
    private DebtService debtService;
    private ExpenditureCategoryService expCatService;
    private TransactionsValidator validator;
    private SecurityService securityService;

    @Autowired
    public TransactionsManager(TransactionService transactionService, AccountService accountService, DebtService debtService, ExpenditureCategoryService expCatService, TransactionsValidator validator, SecurityService securityService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.debtService = debtService;
        this.expCatService = expCatService;
        this.validator = validator;
        this.securityService = securityService;
    }
    
    
    public RedirectAttributes deleteTransaction(int id, RedirectAttributes redirectAttributes) {
        ExchangeManager exManager = new ExchangeManager();
        Transaction transaction = transactionService.getTransactionById(id);

        switch(transaction.getType()) {
            case "incoming": {
                String fromCurrency = transaction.getCurrency();
                String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
                float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());
                Account tempAccount = accountService.getAccount(transaction.getAccountNumberTo());
                float tempBalance = (float) (tempAccount.getBalance() - amount);
                tempAccount.setBalance(tempBalance);
                accountService.updateAccount(tempAccount);
                break;
            }
            
            case "outgoing": {
                Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
                float tempBalance = tempAccount.getBalance() + transaction.getAmount();
                tempAccount.setBalance(tempBalance);
                accountService.updateAccount(tempAccount);
                break;
            }
            
            case "between": {
                String fromCurrency = transaction.getCurrency();
                String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
                float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());
                Account tempAccountFrom = accountService.getAccount(transaction.getAccountNumberFrom());
                Account tempAccountTo = accountService.getAccount(transaction.getAccountNumberTo());
                tempAccountFrom.setBalance(tempAccountFrom.getBalance() + transaction.getAmount());
                tempAccountTo.setBalance(tempAccountTo.getBalance() - amount);
                accountService.updateAccount(tempAccountTo);
                accountService.updateAccount(tempAccountFrom);
                break;
            }
            
            case "deposit": {
                Account tempAccount = accountService.getAccount(transaction.getAccountNumberTo());
                tempAccount.setBalance(tempAccount.getBalance() - transaction.getAmount());
                accountService.updateAccount(tempAccount);
                break;
            }
            
            case "withdraw": {
                Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
                tempAccount.setBalance(tempAccount.getBalance() + transaction.getAmount());
                accountService.updateAccount(tempAccount);
                break;
            }
            
            case "debt": {
                Account tempAccount = accountService.getAccount(transaction.getAccountNumberFrom());
                tempAccount.setBalance(tempAccount.getBalance() + transaction.getAmount());
                accountService.updateAccount(tempAccount);
                
                Debt tempDebt = debtService.getDebtById(transaction.getDebtId());
                String currency = tempDebt.getCurrency();
                float convertedAmount = (float) exManager.convertCurrency(currency, transaction.getCurrency(), transaction.getAmount());
                tempDebt.setAmount(tempDebt.getAmount() + convertedAmount);
                debtService.updateDebt(tempDebt);
                break;
            }
            
            default: {
                redirectAttributes.addFlashAttribute("error", "Something went wrong. Unknown type of transaction.");
                return redirectAttributes;
            }
        }       
        
        transactionService.deleteTransaction(id);
        redirectAttributes.addAttribute("success", "delete");
        return redirectAttributes;
    }

    public RedirectAttributes addNewTransaction(Transaction transaction, RedirectAttributes redirectAttributes) {
        if (!validator.isDebtRepaymentOptionSet(transaction)) {
            transaction.setIfIsDebtRepayment("no");
        }        
        if (validator.isDateSet(transaction)) {
            transaction.setDateTime(transaction.getDate().atStartOfDay());
        }             
        String username = securityService.getUsernameFromSecurityContext();                               
        transaction.setUsername(username);
                 
        if (!validator.isCurrencySet(transaction)) {
            transaction = setCurrency(transaction);
        }
        transaction = correctExpenditureTypeIfWrong(transaction);
        
        if (validator.doesRequireDescription(transaction)) {
            redirectAttributes = validator.validateDescription(transaction.getDescription(), redirectAttributes);
            if (haveError(redirectAttributes)) {
                return redirectAttributes;
            }
            transaction.setDescription(formatDescription(transaction.getDescription()));
        }
        
        redirectAttributes = affectAccountsAndDebts(transaction, redirectAttributes);
        if (haveError(redirectAttributes)) {
            return redirectAttributes;
        }
        
        transactionService.saveTransaction(transaction);        
        
        if (isDebtRepayment(transaction)) {
            redirectAttributes.addFlashAttribute("success", "Transaction was successfully created.");
        }
        else {
            redirectAttributes.addAttribute("success", "added");  
        }        
        return redirectAttributes;
    }
    
    public Map searchTransactions(TransactionSearch searchTemplate, RedirectAttributes redirectAttributes) {
        Map result = new HashMap();
        List<Transaction> transactions = getAllTransactions(searchTemplate);     
                
        if (transactions == null) {
            redirectAttributes.addAttribute("error", "emptyList");
            result.put("redirectAttributes", redirectAttributes);
            return result;
        }        
        if (validator.isSearchTemplateEmpty(searchTemplate)) {
            redirectAttributes.addAttribute("error", "searchNull");
            result.put("redirectAttributes", redirectAttributes);
            return result;
        }       
        
        List<Transaction> foundTransactions = filterTransactionList(transactions, searchTemplate);
        if (foundTransactions.isEmpty()) {
            redirectAttributes.addAttribute("error", "noTransactions");
        }
        else {
            result.put("transactions", foundTransactions);
        }
        result.put("redirectAttributes", redirectAttributes);
        return result;
    }
    
    public static boolean isDebtRepayment(Transaction transaction) {
        if (transaction.getIfIsDebtRepayment().equals("yes")) {
            return true;
        }
        return false;
    }
    
    public boolean isTransactionPlanned(Transaction transaction) {
        if (transaction.getDate()!= null && transaction.getDate().isAfter(LocalDate.now(ZoneId.of("UTC+4")))) {
            return true;
        }
        return false;
    }
    
    public static boolean isSearchTemplatePlanned(TransactionSearch searchTemplate) {
        if (searchTemplate.getIsPlanned().equals("yes")) {
            return true;
        }
        return false;
    }
        
    public static String formatDescription(String description) {
        return description.trim().replaceAll(" +", " ");
    }
    
    private Transaction setCurrency(Transaction transaction) {
        if (!transaction.getType().equals("incoming")) {
            if (transaction.getType().equals("deposit")) {
                transaction.setCurrency(accountService.getAccount(transaction.getAccountNumberTo()).getCurrency());
            }
            else {
                transaction.setCurrency(accountService.getAccount(transaction.getAccountNumberFrom()).getCurrency());
            }
        }
        return transaction;
    }
    
    private Transaction correctExpenditureTypeIfWrong(Transaction transaction) {
        if (transaction.getType().equals("outgoing") && transaction.getExpenditureType().isBlank()) {
            transaction.setExpenditureType(null);
        }
        return transaction;
    }
    
    private RedirectAttributes affectAccountsAndDebts(Transaction transaction, RedirectAttributes redirectAttributes) {
        switch (transaction.getType()) {            
            case "incoming": {
                ExchangeManager exManager = new ExchangeManager();
                String fromCurrency = transaction.getCurrency();
                String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
                float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());
                Account account = accountService.getAccount(transaction.getAccountNumberTo());
                float tempBalance = (float) (account.getBalance() + amount);
                account.setBalance(tempBalance);
                accountService.updateAccount(account);
                break;
            }            
            case "outgoing": {
                redirectAttributes = validator.validateAmount(transaction, redirectAttributes);
                if (haveError(redirectAttributes)) {
                    return redirectAttributes;
                }                
                Account account = accountService.getAccount(transaction.getAccountNumberFrom());
                float balance = account.getBalance() - transaction.getAmount();
                account.setBalance(balance);
                accountService.updateAccount(account);
                break;
            }
            case "between": {
                redirectAttributes = validator.validateAmount(transaction, redirectAttributes);
                if (haveError(redirectAttributes)) {
                    return redirectAttributes;
                }
                
                ExchangeManager exManager = new ExchangeManager();
                String fromCurrency = transaction.getCurrency();
                String toCurrency = accountService.getAccount(transaction.getAccountNumberTo()).getCurrency();
                float amount = (float) exManager.convertCurrency(toCurrency, fromCurrency, transaction.getAmount());
                Account accountFrom = accountService.getAccount(transaction.getAccountNumberFrom());
                Account accountTo = accountService.getAccount(transaction.getAccountNumberTo());
                
                accountFrom.setBalance(accountFrom.getBalance() - transaction.getAmount());
                accountTo.setBalance(accountTo.getBalance() + amount);
                accountService.updateAccount(accountTo);
                accountService.updateAccount(accountFrom);
                break;
            }
            case "deposit": {
                Account tempAccount = accountService.getAccount(transaction.getAccountNumberTo());
                tempAccount.setBalance(tempAccount.getBalance() + transaction.getAmount());
                accountService.updateAccount(tempAccount);
                break;
            }
            case "withdraw": {
                redirectAttributes = validator.validateAmount(transaction, redirectAttributes);
                if (haveError(redirectAttributes)) {
                    return redirectAttributes;
                }
                
                Account account = accountService.getAccount(transaction.getAccountNumberFrom());
                account.setBalance(account.getBalance() - transaction.getAmount());
                accountService.updateAccount(account);
                break;
            }
            case "debt": {
                redirectAttributes = validator.validateAmount(transaction, redirectAttributes);
                if (haveError(redirectAttributes)) {
                    return redirectAttributes;
                }
                
                ExchangeManager exManager = new ExchangeManager();
                Debt debt = debtService.getDebtById(transaction.getDebtId());
                Account account = accountService.getAccount(transaction.getAccountNumberFrom());
                account.setBalance(account.getBalance() - transaction.getAmount());
                accountService.updateAccount(account);    
                String currency = debt.getCurrency();
                float convertedAmount = (float) exManager.convertCurrency(currency, transaction.getCurrency(), transaction.getAmount());
                debt.setAmount(debt.getAmount() - convertedAmount);
                debtService.updateDebt(debt);
                break;
            }
            default: {
                redirectAttributes.addFlashAttribute("error", "Something went wrong. Unknown type of transaction!");
            }
        }
        return redirectAttributes;
    }   
    
    private List<Transaction> getAllTransactions(TransactionSearch searchTemplate) {
        List<Transaction> transactions;
        if (isSearchTemplatePlanned(searchTemplate)) {
            transactions = transactionService.getPlannedTransactions();
        }            
        else {
            transactions = transactionService.getTransactions();
        }
        return transactions;
    }
    
    private List<Transaction> filterTransactionList(List<Transaction> transactions, TransactionSearch searchTemplate) {
        List<Transaction> result = new ArrayList();        
        for (Transaction item : transactions) {
            if (validator.doesTransactionMatchCriteria(item, searchTemplate)) {
                result.add(item);
            }
        }
        return result;
    }
}
