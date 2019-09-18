package com.personalbudget.demo.account.logics;

import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.account.service.AccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
class AccountValidator {
    
    private AccountService accountService;
    
    @Autowired
    AccountValidator(AccountService accountService) {
        this.accountService = accountService;
    }
    
    Model validateFieldsCompletion(Model model, Account theAccount) {
        if (theAccount.getAccountName() == null || theAccount.getAccountNumber() == null || theAccount.getBank() == null || theAccount.getCurrency() == null
                || theAccount.getAccountName().isBlank() || theAccount.getAccountNumber().isBlank() || theAccount.getBank().isBlank() || theAccount.getCurrency().isBlank()) {
            model.addAttribute("error", "All fields must be completed.");
            return model;
        }
        return model;
    }
    
    Model validateAccountName(Model model, Account theAccount) {
        if ((theAccount.getAccountName().trim().length() < 4) || (theAccount.getAccountName().trim().length() > 30)) {
            model.addAttribute("error", "Account name must be between 4 and 30 characters long.");
            return model;
        }       
        return model;
    }
    
    Model validateBankName(Model model, Account theAccount) {
        if ((theAccount.getBank().trim().length() < 3) || (theAccount.getBank().trim().length() > 40)) {
            model.addAttribute("error", "Bank name must be between 3 and 40 characters long.");
            return model;
        }        
        return model;
    }
    
    Model validateAccountNumberFormat(Model model, String accountNumber) {        
        boolean tempFlag = true;        
        for (int i=0; i<accountNumber.length(); i++)
            if (!Character.isDigit(accountNumber.charAt(i)))
                tempFlag = false;
        
        if (!tempFlag) {
            model.addAttribute("error", "Wrong account number.");
            return model;
        }
                
        if (accountNumber.length() != 26) {
            model.addAttribute("error", "Account number must be 26 numbers long.");
            return model;
        }
        return model;
    }
    
    Model validateAccountNumberAvailability(Model model, String accountNumber) {
        List<String> tempAccountList = accountService.getAllAccountNumbers();        
        for (String item : tempAccountList) {
            if (item.equals(accountNumber)) {
                model.addAttribute("error", "That account number is already taken.");
                return model;
            }
        }
        return model;
    }
    
    boolean isAccountOwnedByCurrentUser(String accountNumber) {
        List<Account> accounts = accountService.getAccounts();        
        for (Account item : accounts) {
            if (item.getAccountNumber().equals(accountNumber)) {
                return true;
            }
        }
        return false;
    }
}
