package com.personalbudget.demo.account.dao;

import com.personalbudget.demo.account.entity.Account;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import org.hibernate.Session;
import org.hibernate.query.Query;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class AccountDAOImplTest {    
        
    @Autowired
    private AccountDAO accountDAOImpl;
       
    @Autowired
    private EntityManager entityManager;
    
    @Test
    @Transactional
    @WithMockUser(username="a")
    public void getAccountsShouldNotReturnNull() {
        assertNotNull(accountDAOImpl.getAccounts());
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getAccountShouldReturnNull() {
        assertNull(accountDAOImpl.getAccount("none"));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getAccountShouldReturnProperAccount() {
        // given
        String accountNumber = "foo";
        Account account;        
        // when
        account = accountDAOImpl.getAccount(accountNumber);
        // then
        assertNotNull(account, "getAccount() returned null.");
        assertEquals(account.getUsername(), SecurityContextHolder.getContext().getAuthentication().getName(), "Wrong 'username' field in returned account.");
        assertEquals(account.getAccountNumber(), accountNumber, "Wrong 'accountNumber' field in returned account.");
        assertNotNull(account.getBank(), "'bank' field not set.");
        assertNotNull(account.getBalance(), "'balance' field not set.");
        assertThat("Balance is negative.", account.getBalance(), is(greaterThanOrEqualTo((float) 0)));
        assertNotNull(account.getCurrency(), "'currency' field not set.");
        assertNotNull(account.getAccountName(), "'accountName' field not set.");
    }
    
    
    @Test
    @Transactional
    @WithMockUser(username="i")
    public void getAllAccountNumbersShouldNotReturnNull() {
        assertNotNull(accountDAOImpl.getAllAccountNumbers());
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getAllAccountNumbersShouldReturnProperNumbers() {
        // given
        List<String> accountNumbers;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Session session = entityManager.unwrap(Session.class);
        Query<Account> query = session.createQuery("from accounts where username='" + username + "'", Account.class);        
        List<Account> accounts = query.getResultList();
        boolean areTheSame = true;
        // when
        accountNumbers = accountDAOImpl.getAllAccountNumbers();
        // then
        if (accounts.size() == accountNumbers.size()) {
            for (int i=0; i<accounts.size(); i++) {
                if (!accounts.get(i).getAccountNumber().equals(accountNumbers.get(i))) {
                    areTheSame = false;
                }
            }
        }
        else {
            areTheSame = false;
        }
        assertThat(areTheSame, is(true));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test") 
    public void saveAccountTest() {
        // given
        Account account = new Account();
        Account accountFromDB;
        String accountName = "kguigiudfng";
        String accountNumber = "poikvcn";
        float balance = (float) 15.87;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String bank = "Z";
        String currency = "PLN";
        
        account.setAccountName(accountName);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setBank(bank);
        account.setCurrency(currency);
        account.setUsername(username);
        
        Session session = entityManager.unwrap(Session.class);
        Query<Account> query = session.createQuery("from accounts where (username='" + username + "' AND account_number='" + accountNumber + "')", Account.class);        
        // when
        accountDAOImpl.saveAccount(account);                
        // then
        accountFromDB = query.getSingleResult();
        assertEquals(accountFromDB, account);
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void removeAccountTest() {
        // given
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String accountNumber = "foo";
        Session session = entityManager.unwrap(Session.class);
        Query<Account> query = session.createQuery("from accounts where (username='" + username + "' AND account_number='" + accountNumber + "')", Account.class);
        Account checkAccount;
        // when
        accountDAOImpl.removeAccount(accountNumber);
        // then
        try {
            checkAccount = query.getSingleResult();
        }
        catch (NoResultException ex) {
            checkAccount = null;
        }
        assertThat(checkAccount, is(nullValue()));
    }        
}