package com.personalbudget.demo.account.service;

import com.personalbudget.demo.account.entity.Account;
import java.util.List;


public interface AccountService {

    public List<Account> getAccounts();
    
    public Account getAccount(String theAccountNumber);
    
    public void saveAccount(Account theAccount);
    
    public void updateAccount(Account theAccount);
    
    public void removeAccount(String theAccountNumber);
        
    public List<String> getAllAccountNumbers();
}
