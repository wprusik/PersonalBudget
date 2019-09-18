package com.personalbudget.demo.debt.logics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.personalbudget.demo.debt.entity.Debt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.security.test.context.support.WithMockUser;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@WithMockUser(username="test")
@SpringBootTest
public class DebtValidatorTest {

    @Autowired
    private DebtValidator validator;
    
    private RedirectAttributes redirectAttributes;
    private Debt debt;
    
    @BeforeEach
    private void prepareMethod() {
        redirectAttributes = new RedirectAttributesModelMap();
        debt = new Debt();
        debt.setAmount(15);
        debt.setCreditor("test creditor");
        debt.setCurrency("PLN");
        debt.setDebtName("test name");
        debt.setType("debt");
    }
    
    @Test
    public void validateDebtShouldReturnError() {
        // when
        debt.setDebtName("   Test debt      ");
        validator.validateDebt(debt, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void validateDebtShouldNotReturnError() {
        // when
        redirectAttributes = validator.validateDebt(debt, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
}