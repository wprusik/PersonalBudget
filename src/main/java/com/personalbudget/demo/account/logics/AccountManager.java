package com.personalbudget.demo.account.logics;

import static com.personalbudget.demo.CommonTools.haveError;
import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.account.service.AccountService;
import com.personalbudget.demo.currency.service.CurrencyService;
import com.personalbudget.demo.security.SecurityService;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.transaction.service.TransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class AccountManager {

    private AccountService accountService;
    private TransactionService transactionService;
    private CurrencyService currencyService;
    private SecurityService securityService;    
    private AccountValidator validator;
    
    @Autowired
    public AccountManager(AccountService accountService, TransactionService transactionService, CurrencyService currencyService, SecurityService securityService, AccountValidator validator) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.currencyService = currencyService;
        this.securityService = securityService;
        this.validator = validator;
    }
    
    
    public void deleteAccount(String accountNumber) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        for (Transaction item : transactions){
            if (item.getAccountNumberFrom() != null)
                if (item.getAccountNumberFrom().equals(accountNumber))
                    transactionService.deleteTransaction(item.getTransactionId());
        }
        
        accountService.removeAccount(accountNumber);
    }
    
    public Model addNewAccount(Model model, Account theAccount) {        
        model.addAttribute("account", new Account());
        model.addAttribute("currencies", currencyService.getCurrencies());
        model = validator.validateFieldsCompletion(model, theAccount);
        if (haveError(model)) {
            return model;
        }
        
        String accountNumber = theAccount.getAccountNumber().trim().replaceAll(" ","").replaceAll(",", "");
        String username = securityService.getUsernameFromSecurityContext();
        
        model = validator.validateBankName(model, theAccount);
        model = validator.validateAccountNumberFormat(model, accountNumber);
        model = validator.validateAccountNumberAvailability(model, accountNumber);
        model = validator.validateAccountName(model, theAccount);     
        
        if (haveError(model)) {
            return model;
        }
        
        theAccount.setUsername(username);
        theAccount.setBank(theAccount.getBank().trim());
        theAccount.setAccountName(theAccount.getAccountName().trim());
        theAccount.setAccountNumber(accountNumber);        
        accountService.saveAccount(theAccount);
        return model;
    }
    
    public Model editAccount(Model model, Account theAccount) {            
        String username = securityService.getUsernameFromSecurityContext();        
        theAccount.setUsername(username);
                
        model = validator.validateFieldsCompletion(model, theAccount);
        if (haveError(model)) {
            return model;
        }
                
        model = validator.validateBankName(model, theAccount);
        model = validator.validateAccountName(model, theAccount);
        if (haveError(model)) {
            return model;
        }
        
        theAccount.setBank(theAccount.getBank().trim());
        theAccount.setAccountName(theAccount.getAccountName().trim());               
        accountService.updateAccount(theAccount);
        return model;
    }
    
    public RedirectAttributes checkAccountNumberValidity(String accountNumber, RedirectAttributes redirectAttributes) {
        if (!(validator.isAccountOwnedByCurrentUser(accountNumber))) {
            redirectAttributes.addFlashAttribute("error", "Incorrect account number. Please don't manually change the URL.");
        }
        return redirectAttributes;
    }
}
