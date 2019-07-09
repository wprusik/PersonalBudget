package com.personalbudget.demo.Service;

import com.personalbudget.demo.Entity.Account;
import java.util.List;


public interface AccountService {

    public List<Account> getAccounts();
    
    public Account getAccount(String theAccountNumber);
    
    public void saveAccount(Account theAccount);
    
    public void updateAccount(Account theAccount);
    
    public void removeAccount(String theAccountNumber);
    
    public List<String> getAllAccountNumbers();
}
