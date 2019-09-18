package com.personalbudget.demo.transaction.logics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.personalbudget.demo.transaction.dto.TransactionSearch;
import com.personalbudget.demo.transaction.entity.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import org.hibernate.Session;
import org.hibernate.query.Query;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@WithMockUser(username="test")
@SpringBootTest
public class TransactionsManagerTest {

    @Autowired
    private TransactionsManager manager;
    
    @Autowired
    private EntityManager entityManager;
    
    private RedirectAttributes redirectAttributes;
    private TransactionSearch searchTemplate;
    private Transaction transaction;
    
    @BeforeEach
    private void prepareMethod() {
        redirectAttributes = new RedirectAttributesModelMap();
        transaction = new Transaction();
        searchTemplate = new TransactionSearch();
    }
    
    @Test
    public void deleteTransactionShouldReturnError() {
        // when
        redirectAttributes = manager.deleteTransaction(28, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    @Transactional
    public void deleteTransactionShouldRemoveTransactionAndAffectAccount() {
        // given
        Session session = entityManager.unwrap(Session.class);
        Query<Transaction> queryTransaction = session.createQuery("from transactions where id='1'", Transaction.class);
        String accountNumber = queryTransaction.getSingleResult().getAccountNumberFrom();
        Query<Float> queryBalance = session.createQuery("select balance from accounts where accountNumber='" + accountNumber + "'", Float.class);
        Float balance = (float) -1;
        Float balanceCheck = (float) queryBalance.getSingleResult();        
        // when
        redirectAttributes = manager.deleteTransaction(1, redirectAttributes);
        // then
        try {            
            balance = (float) queryBalance.getSingleResult();
            transaction = queryTransaction.getSingleResult();            
        }
        catch (NoResultException ex) {
            if (balance==-1) {
                fail("Error retrieving account balance from database");
            }            
            transaction = null;
        }       
        assertThat("Transaction has not been removed from database", transaction, is(nullValue()));
        assertThat("The account has not been affected", balance, either(greaterThan(balanceCheck)).or(lessThan(balanceCheck)));
    }
    
    @Test
    public void addNewTransactionShouldReturnError() {
        // given
        transaction.setAccountNumberFrom("86578643065942356403625734");
        transaction.setAccountNumberTo("foo");
        transaction.setAmount(15);
        transaction.setCurrency("PLN");
        transaction.setDateTime(LocalDateTime.now());
        transaction.setDescription("a");
        transaction.setType("between");
        // when
        redirectAttributes = manager.addNewTransaction(transaction, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    @Transactional
    public void addNewTransactionShouldSaveTransactionAndReturnSuccess() {
        // given
        String description = "test description";
        transaction.setAccountNumberFrom("86578643065942356403625734");
        transaction.setAccountNumberTo("foo");
        transaction.setAmount(15);
        transaction.setCurrency("PLN");
        transaction.setDateTime(LocalDateTime.now());
        transaction.setDescription(description);
        transaction.setType("between");
        transaction.setIfIsDebtRepayment("no");
        Session session = entityManager.unwrap(Session.class);
        Query<Transaction> query = session.createQuery("from transactions where (transactionId=(select max(transactionId) from transactions) and description='" + description + "')", Transaction.class);
        // when
        redirectAttributes = manager.addNewTransaction(transaction, redirectAttributes);
        // then
        try {
            transaction = query.getSingleResult();
        }
        catch (NoResultException ex) {
            transaction = null;
        }
        assertThat("Has not returned 'success' attribute", redirectAttributes.asMap().containsKey("success"), is(true));
        assertThat("Transaction has not been saved", transaction, not(nullValue()));
    }
    
    @Test
    public void searchTransactionsShouldReturnError() {
        // given
        Map result = new HashMap();
        searchTemplate.setCurrency("");
        searchTemplate.setDescription("");
        searchTemplate.setFromAccount("");
        searchTemplate.setToAccount("");
        searchTemplate.setIsPlanned("");        
        // when
        result = manager.searchTransactions(searchTemplate, redirectAttributes);
        // then
        assertThat(((RedirectAttributes)result.get("redirectAttributes")).asMap().containsKey("error"), is(true));
    }
    
    @Test
    public void searchTransactionsShouldReturnNonEmptyList() {
        // given
        Map result = new HashMap();
        searchTemplate.setCurrency("PLN");
        searchTemplate.setDescription("");
        searchTemplate.setFromAccount("");
        searchTemplate.setToAccount("");
        searchTemplate.setIsPlanned("");  
        // when
        result = manager.searchTransactions(searchTemplate, redirectAttributes);
        // then
        assertThat((List<Transaction>)result.get("transactions"), not(empty()));
    }
    
    @Test
    public void isDebtRepaymentShouldReturnTrue() {
        // given
        transaction.setIfIsDebtRepayment("yes");
        // when/then
        assertTrue(manager.isDebtRepayment(transaction));
    }
    
    @Test
    public void isDebtRepaymentShouldReturnFalse() {
        // given
        transaction.setIfIsDebtRepayment("no");
        // when/then
        assertFalse(manager.isDebtRepayment(transaction));
    }
    
    @Test
    public void isTransactionPlannedShouldReturnTrue() {
        // given
        LocalDate date = LocalDate.of(2700, Month.MARCH, 1);
        transaction.setDate(date);
        // when/then
        assertTrue(manager.isTransactionPlanned(transaction));
    }
    
    @Test
    public void isTransactionPlannedShouldReturnFalse() {
        // given
        LocalDate date = LocalDate.of(1990, Month.MARCH, 1);
        transaction.setDate(date);
        // when/then
        assertFalse(manager.isTransactionPlanned(transaction));
    }
    
    @Test
    public void isSearchTemplatePlannedShouldReturnTrue() {
        // given
        searchTemplate.setIsPlanned("yes");
        // when/then
        assertTrue(manager.isSearchTemplatePlanned(searchTemplate));
    }
    
    @Test
    public void isSearchTemplatePlannedShouldReturnFalse() {
        // given
        searchTemplate.setIsPlanned("no");
        // when/then
        assertFalse(manager.isSearchTemplatePlanned(searchTemplate));
    }
    
    @Test
    public void formatDescriptionShouldRemoveAllWhiteSpace() {
        // given
        String description = "     a    b  c   ";
        String descriptionCheck = "a b c";
        // when
        description = manager.formatDescription(description);
        // then
        assertEquals(description, descriptionCheck);
    }
}