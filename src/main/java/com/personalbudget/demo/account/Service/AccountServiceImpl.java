package com.personalbudget.demo.account.service;


import com.personalbudget.demo.account.dao.AccountDAO;
import com.personalbudget.demo.account.entity.Account;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountDAO accountDAO;
    
    @Override
    @Transactional
    public List<Account> getAccounts() {
        return accountDAO.getAccounts();
    }

    @Override
    @Transactional
    public Account getAccount(String theAccountNumber) {
        return accountDAO.getAccount(theAccountNumber);
    }

    @Override
    @Transactional
    public void saveAccount(Account theAccount) {
        accountDAO.saveAccount(theAccount);
    }

    @Override
    @Transactional
     public void updateAccount(Account theAccount) {        
        accountDAO.updateAccount(theAccount);
    }

    @Override
    @Transactional
    public void removeAccount(String theAccountNumber) {
        accountDAO.removeAccount(theAccountNumber);
    }

    @Override
    @Transactional
    public List<String> getAllAccountNumbers() {
        return accountDAO.getAllAccountNumbers();
    }
}
