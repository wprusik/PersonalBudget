package com.personalbudget.demo.account.logics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import com.personalbudget.demo.account.entity.Account;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.test.context.support.WithMockUser;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class AccountValidatorTest {
    
    @Autowired
    private AccountValidator validator;
    
    private Model model;
    private Account account;
    
    @BeforeEach
    private void prepareMethod() {
        model = new ExtendedModelMap();
        account = new Account();
    }
    
    @Test
    public void validateFieldsCompletionShouldReturnError() {
        // when          
        model = validator.validateFieldsCompletion(model, account);
        // then
        assertThat(model.containsAttribute("error"), is(true));
    }
    
    @Test
    public void validateAccountNameShouldReturnError() {
        // given
        Model modelLong = new ExtendedModelMap();
        Account accountWithTooLongName = new Account();
        account.setAccountName("a");
        accountWithTooLongName.setAccountName("aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaa");
        // when
        model = validator.validateAccountName(model, account);
        modelLong = validator.validateAccountName(modelLong, accountWithTooLongName);
        // then
        assertThat("Account name too short - no error", model.containsAttribute("error"), is(true));
        assertThat("Account name too long - no error", modelLong.containsAttribute("error"), is(true));
    }
    
    @Test
    public void validateAccountNameShouldNotReturnError() {
        // given  
        account.setAccountName("proper name");
        // when
        model = validator.validateAccountName(model, account);
        // then
        assertThat(model.containsAttribute("error"), is(false));
    }
    
    @Test
    public void validateBankNameShouldReturnError() {
        // given
        Model modelLong = new ExtendedModelMap();
        Account accountWithTooLongBankName = new Account();
        account.setBank("a");
        accountWithTooLongBankName.setBank("aaaaaaaaaabbbbbbbbbbaaaaaaaaaabbbbbbbbbbaaaaaaaaaa");
        // when
        model = validator.validateBankName(model, account);
        modelLong = validator.validateBankName(modelLong, accountWithTooLongBankName);
        // then
        assertThat("Bank name too short - no error", model.containsAttribute("error"), is(true));
        assertThat("Bank name too long - no error", modelLong.containsAttribute("error"), is(true));
    }
    
    @Test
    public void validateBankNameShouldNotReturnError() {
        // given      
        account.setBank("proper bank name");
        // when
        model = validator.validateBankName(model, account);
        // then
        assertThat(model.containsAttribute("error"), is(false));
    }
    
    @Test
    public void validateAccountNumberFormatShouldReturnError() {
        // when
        model = validator.validateAccountNumberFormat(model, "wrong number");
        // then
        assertThat(model.containsAttribute("error"), is(true));
    }
    
    @Test
    public void validateAccountNumberFormatShouldNotReturnError() {
        // when
        model = validator.validateAccountNumberFormat(model, "12345678901234567890123456");
        // then
        assertThat(model.containsAttribute("error"), is(false));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateAccountNumberAvailabilityShouldReturnError() {
        // when
        model = validator.validateAccountNumberAvailability(model, "97390856749836278538754387");
        // then
        assertThat(model.containsAttribute("error"), is(true));
    }
    
    @Test
    @WithMockUser(username="test")
    public void validateAccountNumberAvailabilityShouldNotReturnError() {
        // when
        model = validator.validateAccountNumberAvailability(model, "wrong account number");
        // then
        assertThat(model.containsAttribute("error"), is(false));
    }
    
    @Test
    @WithMockUser(username="test")
    public void isAccountOwnedByCurrentUserShouldReturnTrue() {
        assertTrue(validator.isAccountOwnedByCurrentUser("97390856749836278538754387"));
    }
    
    @Test
    @WithMockUser(username="test")
    public void isAccountOwnedByCurrentUserShouldReturnFalse() {
        assertFalse(validator.isAccountOwnedByCurrentUser("nope"));
    }   
}