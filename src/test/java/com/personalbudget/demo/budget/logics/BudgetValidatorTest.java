package com.personalbudget.demo.budget.logics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.budget.entity.Budget;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@WithMockUser(username="test")
@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class BudgetValidatorTest {

    @Autowired
    BudgetValidator validator;
    
    @Test
    public void isExpenditureCorrectShouldReturnFalse() {
        assertFalse(validator.isExpenditureCorrect(3, 1));
    }
    
    @Test
    public void isExpenditureCorrectShouldReturnTrue() {
        assertTrue(validator.isExpenditureCorrect(1, 1));
    }
    
    @Test
    public void isBudgetCorrectShouldReturnFalse() {
        assertFalse(validator.isBudgetCorrect(3));
    }
    
    @Test
    public void isBudgetCorrectShouldReturnTrue() {
        assertTrue(validator.isBudgetCorrect(1));
    }
    
    @Test
    public void isExpenditureDescriptionCorrectShouldReturnFalse() {
        assertFalse(validator.isExpenditureDescriptionCorrect("a"));
    }
    
    @Test
    public void isExpenditureDescriptionCorrectShouldReturnTrue() {
        assertTrue(validator.isExpenditureDescriptionCorrect("correct"));
    }
    
    @Test
    public void validateBudgetShouldReturnError() {
        // given
        Budget budget = new Budget();
        Budget budget2 = new Budget();
        budget.setBudgetName("correct");
        budget.setPurpose("a");
        budget2.setBudgetName("Przeprowadzka");
        budget2.setPurpose("correct");
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        RedirectAttributes redirectAttributes2 = new RedirectAttributesModelMap();
        // when
        redirectAttributes = validator.validateBudget(budget, redirectAttributes);
        redirectAttributes2 = validator.validateBudget(budget2, redirectAttributes2);
        // then
        assertThat("Wrong purpose set, no error returned", redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
        assertThat("Existing name set, no error returned", redirectAttributes2.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void validateBudgetShouldNotReturnError() {
        // given
        Budget budget = new Budget();
        budget.setBudgetName("correct");
        budget.setPurpose("correct");
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        // when
        redirectAttributes = validator.validateBudget(budget, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
}