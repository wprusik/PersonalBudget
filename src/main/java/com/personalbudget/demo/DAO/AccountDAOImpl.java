package com.personalbudget.demo.DAO;

import com.personalbudget.demo.Entity.Account;
import com.personalbudget.demo.Entity.User;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDAOImpl implements AccountDAO {

    private EntityManager entityManager;
    
    @Autowired
    public AccountDAOImpl(EntityManager theEntityManager)
    {   
        entityManager = theEntityManager;
    }
    
    @Override
    public List<Account> getAccounts() {
        Session currentSession = entityManager.unwrap(Session.class);
        
        // get username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        Query<Account> theQuery = currentSession.createQuery("from accounts WHERE username='" + username + "'", Account.class);
        List<Account> accounts = (List<Account>) theQuery.getResultList();
                
        return accounts;
    }

    @Override
    public Account getAccount(String theAccountNumber) {
        Session currentSession = entityManager.unwrap(Session.class);
        
        // get username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        String myQuery = "FROM accounts WHERE (username='" + username + "' AND account_number='" + theAccountNumber + "')";
        Query<Account> theQuery = currentSession.createQuery(myQuery, Account.class);
        Account theAccount = (Account) theQuery.getSingleResult(); 
       
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
        
        // get username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        String myQuery = "FROM accounts WHERE (username='" + username + "' AND account_number='" + theAccountNumber + "')";
        Query<Account> theQuery = currentSession.createQuery(myQuery, Account.class);
        Account theAccount = (Account) theQuery.getSingleResult();
        
        currentSession.delete(theAccount);
    }

    @Override
    public List<String> getAllAccountNumbers() {
        Session currentSession = entityManager.unwrap(Session.class);
        
        // get username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        String myQuery = "FROM accounts WHERE (username='" + username + "')";
        Query<Account> theQuery = currentSession.createQuery(myQuery, Account.class);
        List<Account> accounts = (List<Account>) theQuery.getResultList();
        
        List<String> tempList = new LinkedList<String>();
        
        for (Account item : accounts)
            tempList.add(item.getAccountNumber());
        
        return tempList;
    }

}