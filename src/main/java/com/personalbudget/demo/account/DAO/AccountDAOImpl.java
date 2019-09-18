package com.personalbudget.demo.account.dao;

import com.personalbudget.demo.account.entity.Account;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.personalbudget.demo.security.SecurityService;
import javax.persistence.NoResultException;

@Repository
public class AccountDAOImpl implements AccountDAO {

    private EntityManager entityManager;
    private SecurityService securityService;
    
    @Autowired
    public AccountDAOImpl(EntityManager entityManager, SecurityService securityService) {
        this.entityManager = entityManager;
        this.securityService = securityService;
    }
    
    @Override
    public List<Account> getAccounts() {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();
        Query<Account> theQuery = currentSession.createQuery("from accounts WHERE username='" + username + "'", Account.class);
        List<Account> accounts = (List<Account>) theQuery.getResultList();                
        return accounts;
    }

    @Override
    public Account getAccount(String theAccountNumber) {
        Session currentSession = entityManager.unwrap(Session.class);        
        String username = securityService.getUsernameFromSecurityContext();
        
        String myQuery = "FROM accounts WHERE (username='" + username + "' AND account_number='" + theAccountNumber + "')";
        Query<Account> theQuery = currentSession.createQuery(myQuery, Account.class);
        Account theAccount;
        try {
            theAccount = (Account) theQuery.getSingleResult();
        }
        catch (NoResultException ex) {
            theAccount = null;
        }       
        return theAccount;
    }

    @Override
    public void saveAccount(Account theAccount) {
        Session currentSession = entityManager.unwrap(Session.class);        
        currentSession.save(theAccount);
    }

    @Override
    public void updateAccount(Account theAccount) {
        Session currentSession = entityManager.unwrap(Session.class);        
        currentSession.saveOrUpdate(theAccount);
    }

    @Override
    public void removeAccount(String theAccountNumber) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();
        
        String myQuery = "FROM accounts WHERE (username='" + username + "' AND account_number='" + theAccountNumber + "')";
        Query<Account> theQuery = currentSession.createQuery(myQuery, Account.class);
        Account theAccount = (Account) theQuery.getSingleResult();
        
        currentSession.delete(theAccount);
    }

    @Override
    public List<String> getAllAccountNumbers() {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();
        
        String myQuery = "FROM accounts WHERE (username='" + username + "')";
        Query<Account> theQuery = currentSession.createQuery(myQuery, Account.class);
        List<Account> accounts = (List<Account>) theQuery.getResultList();
        
        List<String> tempList = new LinkedList<String>();
        
        for (Account item : accounts)
            tempList.add(item.getAccountNumber());
        
        return tempList;
    }

}