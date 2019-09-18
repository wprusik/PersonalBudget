package com.personalbudget.demo.transaction.logics;

import com.personalbudget.demo.transaction.dto.TransactionSearch;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.transaction.entity.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class TransactionsValidatorTest {

    @Autowired
    private TransactionsValidator validator;
    
    private Transaction transaction;
    private RedirectAttributes redirectAttributes;
    
    @BeforeEach
    private void prepareMethod() {
        transaction = new Transaction();
    }
    
    @Test
    public void isDebtRepaymentOptionSetShouldReturnTrue() {
        transaction.setIfIsDebtRepayment("no");
        assertTrue(validator.isDebtRepaymentOptionSet(transaction));
    }
    
    @Test
    public void isDebtRepaymentOptionSetShouldReturnFalse() {
        assertFalse(validator.isDebtRepaymentOptionSet(transaction));
    }
    
    @Test
    public void isDateSetShouldReturnTrue() {
        transaction.setDate(LocalDate.MAX);
        assertTrue(validator.isDateSet(transaction));
    }
    
    @Test
    public void isDateSetShouldReturnFalse() {
        assertFalse(validator.isDateSet(transaction));
    }
    
    @Test
    public void isCurrencySetShouldReturnTrue() {
        transaction.setCurrency("PLN");
        assertTrue(validator.isCurrencySet(transaction));
    }
    
    @Test
    public void isCurrencySetShouldReturnFalse() {
        assertFalse(validator.isCurrencySet(transaction));
    }
    
    @Test
    public void doesRequireDescriptionShouldReturnTrue() {
        transaction.setType("incoming");
        assertTrue(validator.doesRequireDescription(transaction));
    }
    
    @Test
    public void doesRequireDescriptionShouldReturnFalse() {
        transaction.setType("withdraw");
        assertFalse(validator.doesRequireDescription(transaction));
    }
    
    @Test
    public void validateDescriptionShouldReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        String description = "     a   ";
        // when
        redirectAttributes = validator.validateDescription(description, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void validateDescriptionShouldNotReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        String description = "testing";
        // when
        redirectAttributes = validator.validateDescription(description, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
    
    @Test
    public void isTransactionTypeDebtShouldReturnTrue() {
        transaction.setType("debt");
        assertTrue(validator.isTransactionTypeDebt(transaction));
    }
    
    @Test
    public void isTransactionTypeDebtShouldReturnFalse() {
        transaction.setType("outgoing");
        assertFalse(validator.isTransactionTypeDebt(transaction));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateAmountShouldReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        transaction.setAmount(Float.MAX_VALUE);
        transaction.setAccountNumberFrom("foo");
        transaction.setIfIsDebtRepayment("no");
        transaction.setType("outgoing");
        // when
        redirectAttributes = validator.validateAmount(transaction, redirectAttributes);
        // then
        assertThat(redirectAttributes.asMap().containsKey("error"), is(true));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateAmountShouldNotReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        transaction.setAmount(1);
        transaction.setAccountNumberFrom("foo");
        transaction.setIfIsDebtRepayment("no");
        transaction.setType("outgoing");
        // when
        redirectAttributes = validator.validateAmount(transaction, redirectAttributes);        
        // then
        assertThat(redirectAttributes.asMap().containsKey("error"), is(false));
    }
    
    @Test
    public void isSearchTemplateEmptyShouldReturnTrue() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        searchTemplate.setCurrency("");
        searchTemplate.setDescription("");
        searchTemplate.setFromAccount("");
        searchTemplate.setIsPlanned("");
        searchTemplate.setToAccount("");
        // when/then
        assertTrue(validator.isSearchTemplateEmpty(searchTemplate));
    }
    
    @Test
    public void isSearchTemplateEmptyShouldReturnFalse() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        searchTemplate.setDescription("test description");
        // when/then
        assertFalse(validator.isSearchTemplateEmpty(searchTemplate));
    }
    
    @Test
    public void doesTransactionMatchCriteriaShouldReturnTrue() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        LocalDateTime ldt = LocalDateTime.now();
        transaction.setDescription("test");
        transaction.setAccountNumberFrom("test");
        transaction.setAccountNumberTo("test");
        transaction.setCurrency("PLN");
        transaction.setDateTime(ldt);
        searchTemplate.setDescription("test");
        searchTemplate.setFromAccount("test");
        searchTemplate.setToAccount("test");
        searchTemplate.setCurrency("PLN");
        // when/then
        assertTrue(validator.doesTransactionMatchCriteria(transaction, searchTemplate));
    }
    
    @Test
    public void doesTransactionMatchCriteriaShouldReturnFalse() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        LocalDateTime ldt = LocalDateTime.now();
        transaction.setDescription("test");
        transaction.setAccountNumberFrom("test");
        transaction.setAccountNumberTo("test");
        transaction.setCurrency("PLN");
        transaction.setDateTime(ldt);
        searchTemplate.setDescription("nope");
        searchTemplate.setFromAccount("nope");
        searchTemplate.setToAccount("nope");
        searchTemplate.setCurrency("PLN");
        searchTemplate.setEndDate(new Date());
        // when/then
        assertFalse(validator.doesTransactionMatchCriteria(transaction, searchTemplate));
    }
    
    @Test
    public void isStartDateSetShouldReturnTrue() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        searchTemplate.setStartDate(new Date());
        // when/then
        assertTrue(validator.isStartDateSet(searchTemplate));
    }
    
    @Test
    public void isStartDateSetShouldReturnFalse() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        // when/then
        assertFalse(validator.isStartDateSet(searchTemplate));
    }
    
    @Test
    public void isEndDateSetShouldReturnTrue() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        searchTemplate.setEndDate(new Date());
        // when/then
        assertTrue(validator.isEndDateSet(searchTemplate));
    }
    
    @Test
    public void isEndDateSetShouldReturnFalse() {
        // given
        TransactionSearch searchTemplate = new TransactionSearch();
        // when/then
        assertFalse(validator.isEndDateSet(searchTemplate));
    }
}