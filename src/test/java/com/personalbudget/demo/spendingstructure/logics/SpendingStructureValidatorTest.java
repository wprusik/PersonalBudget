package com.personalbudget.demo.spendingstructure.logics;

import com.personalbudget.demo.account.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.personalbudget.demo.spendingstructure.dto.ChartTemplate;
import com.personalbudget.demo.transaction.entity.Transaction;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class SpendingStructureValidatorTest {

    @Autowired
    private SpendingStructureValidator validator;
    
    private RedirectAttributes redirectAttributes;
    private ChartTemplate template;
    
    @Test
    public void validateFormCompletionShouldReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        template = new ChartTemplate();
        // when
        redirectAttributes = validator.validateFormCompletion(template, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void validateFormCompletionShouldNotReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        template = new ChartTemplate(new Date(1000), new Date(999999), "86578643065942356403625734", "Testowanie");
        // when
        validator.validateFormCompletion(template, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
    
    @Test
    public void isStartDateSetShouldReturnTrue() {
        // given
        template = new ChartTemplate();
        template.setStartDate(new Date(1000));
        // when/then
        assertTrue(validator.isStartDateSet(template));
    }
    
    @Test
    public void isStartDateSetShouldReturnFalse() {
        // given
        template = new ChartTemplate();
        // when/then
        assertFalse(validator.isStartDateSet(template));
    }
    
    @Test
    public void isEndDateSetShouldReturnTrue() {
        // given
        template = new ChartTemplate();
        template.setEndDate(new Date(1000));
        // when/then
        assertTrue(validator.isEndDateSet(template));
    }
    
    @Test
    public void isEndDateSetShouldReturnFalse() {
        // given
        template = new ChartTemplate();
        // when/then
        assertFalse(validator.isEndDateSet(template));
    }
    
    @Test
    public void validateTransactionListShouldReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        List<Transaction> transactions = new ArrayList<Transaction>();
        // when
        redirectAttributes = validator.validateTransactionList(transactions, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void validateTransactionListShouldNotReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(new Transaction());
        // when
        redirectAttributes = validator.validateTransactionList(transactions, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
    
    @Test
    public void validateTimeRangeShouldReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.of(2017, Month.JANUARY, 25, 1, 1, 1);        
        // when
        redirectAttributes = validator.validateTimeRange(start, end, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void validateTimeRangeShouldNotReturnError() {
        // given
        redirectAttributes = new RedirectAttributesModelMap();
        LocalDateTime start = LocalDateTime.of(2019, Month.APRIL, 25, 1, 1, 1);
        LocalDateTime end = LocalDateTime.now();
        // when
        redirectAttributes = validator.validateTimeRange(start, end, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
    
    @Test
    public void isAccountOptionSelectedShouldReturnTrue() {
        // given
        template = new ChartTemplate();
        template.setAccount("86578643065942356403625734");
        // when/then
        assertTrue(validator.isAccountOptionSelected(template));
    }

    @Test
    public void isAccountOptionSelectedShouldReturnFalse() {
        // given
        template = new ChartTemplate();
        // when/then
        assertFalse(validator.isAccountOptionSelected(template));
    }
    
    @Test
    public void doesTransactionMatchAccountAndExpenditureCategoryShouldReturnTrue() {
        // given
        String expType = "Testowanie";
        String numberFrom = "12345678901234567890123456";
                
        Account account = new Account();
        ExpenditureCategory expCat = new ExpenditureCategory();
        Transaction transaction = new Transaction();
        
        account.setAccountNumber(numberFrom);        
        expCat.setExpenditureType(expType);
        transaction.setAccountNumberFrom(numberFrom);
        transaction.setExpenditureType(expType);
        transaction.setCurrency("PLN");
        transaction.setType("outgoing");
        // when/then
        assertTrue(validator.doesTransactionMatchAccountAndExpenditureCategory(transaction, account, expCat));
        
    }
    
    @Test
    public void doesTransactionMatchAccountAndExpenditureCategoryShouldReturnFalse() {
        // given
        String numberFrom = "12345678901234567890123456";                
        Account account = new Account();
        ExpenditureCategory expCat = new ExpenditureCategory();
        Transaction transaction = new Transaction();
        
        account.setAccountNumber(numberFrom);        
        expCat.setExpenditureType("different expenditure type");
        transaction.setAccountNumberFrom(numberFrom);
        transaction.setExpenditureType("Testowanie");
        transaction.setCurrency("PLN");
        transaction.setType("outgoing");
        // when/then
        assertFalse(validator.doesTransactionMatchAccountAndExpenditureCategory(transaction, account, expCat));
    }
    
    @Test
    public void doesTransactionmatchExpenditureCategoryShouldReturnTrue() {
        // given
        Transaction transaction = new Transaction();
        ExpenditureCategory expCat = new ExpenditureCategory();
        String expenditureType = "Testowanie";
        expCat.setExpenditureType(expenditureType);
        transaction.setExpenditureType(expenditureType);
        transaction.setType("outgoing");
        // when/then
        assertTrue(validator.doesTransactionmatchExpenditureCategory(transaction, expCat));
    }
    
    @Test
    public void doesTransactionmatchExpenditureCategoryShouldReturnFalse() {
        // given
        Transaction transaction = new Transaction();
        ExpenditureCategory expCat = new ExpenditureCategory();
        expCat.setExpenditureType("Testowanie");
        transaction.setExpenditureType("different expenditure type");
        transaction.setType("outgoing");
        // when/then
        assertFalse(validator.doesTransactionmatchExpenditureCategory(transaction, expCat));
    }
}