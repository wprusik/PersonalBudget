package com.personalbudget.demo.account.logics;

import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.transaction.entity.Transaction;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.personalbudget.demo.account.dao.AccountDAO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class AccountManagerTest {

    @Autowired
    private AccountManager manager;
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private AccountDAO accountDAO;
    
    private Session currentSession;
    private String username;
    private Model model;
    
    @BeforeEach
    @WithMockUser(username="test")
    private void prepareMethod() {
        currentSession = entityManager.unwrap(Session.class);
        username = SecurityContextHolder.getContext().getAuthentication().getName();
        model = new ExtendedModelMap();
    }
    
    @Test
    @Transactional 
    @WithMockUser(username="test")
    public void deleteAccountShouldDeleteAccountAndAllRelatedTransactions() {
        // given
        String accountNumber = "97390856749836278538754387";        
        Query<Account> queryAccount = currentSession.createQuery(("FROM accounts WHERE account_number='" + accountNumber + "'"), Account.class);
        Query<Transaction> queryTransactions = currentSession.createQuery(("FROM transactions WHERE (username='" + username + "' AND " + "account_number_from='" + accountNumber + "')"), Transaction.class);
        Account account;
        List<Transaction> transactions;
        
        // when
        manager.deleteAccount(accountNumber);        
                
        // then
        try {
            account = (Account) queryAccount.getSingleResult();
        }
        catch (NoResultException ex) {
            account = null;
        }
        transactions = (List<Transaction>) queryTransactions.getResultList();
        
        assertThat("account has not been removed", account, is(nullValue()));
        assertThat("transactions have not been removed" , transactions, is(empty()));
    }

    @Test
    @Transactional
    @WithMockUser(username="test")
    public void addNewAccountShouldReturnError() {
        // given
        Account account = new Account();
        // when
        model = manager.addNewAccount(model, account);        
        // then
        assertThat(model.containsAttribute("error"), is(true));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void addNewAccountShouldSaveAccountAndNotReturnError() {
        // given
        Account account = new Account();
        Account accountFromDB;
        String accountNumber = "12345678901234567890123456";
        account.setAccountName("Account name");
        account.setAccountNumber(accountNumber);
        account.setBalance(15);
        account.setBank("Bank name");
        account.setCurrency("PLN");
        account.setUsername(username);
        // when
        model = manager.addNewAccount(model, account);
        accountFromDB = accountDAO.getAccount(accountNumber);
        // then
        assertThat("Returned error", model.containsAttribute("error"), is(false));
        assertThat("Account not saved", account.equals(accountFromDB), is(true));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void editAccountTest() {
        // given
        String accountNumber = "86578643065942356403625734";
        String properBankName = "test bank";
        Account account = accountDAO.getAccount(accountNumber);        
        boolean hasError;
        // when
        account.setBank("a");
        hasError = manager.editAccount(model, account).containsAttribute("error");
        account.setBank(properBankName);
        model = manager.editAccount(model, account);        
        // then
        assertThat("Error related to wrong 'bank name' expected", hasError, is(true));
        assertThat("Account has not been edited.", account.equals(accountDAO.getAccount(accountNumber)), is(true));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void checkAccountNumberValidityShouldReturnError() {
        // given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String accountNumber = "wrong number";
        // when
        redirectAttributes = manager.checkAccountNumberValidity(accountNumber, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void checkAccountNumberValidityShouldNotReturnError() {
        // given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String accountNumber = "86578643065942356403625734";
        // when
        redirectAttributes = manager.checkAccountNumberValidity(accountNumber, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
}