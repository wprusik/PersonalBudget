package com.personalbudget.demo.expenditurecategory.logics;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@WithMockUser(username="test")
@SpringBootTest
public class ExpenditureCategoryValidatorTest {

    @Autowired
    private ExpenditureCategoryValidator validator;
    
    private ExpenditureCategory category;
    private RedirectAttributes redirectAttributes;
    
    @BeforeEach
    private void prepareMethod() {
        redirectAttributes = new RedirectAttributesModelMap();
        category = new ExpenditureCategory();
        category.setDescription("");
        category.setExpenditureType("");
    }
    
    @Test
    public void areAllFieldsCompletedShouldReturnTrue() {
        // given
        category.setDescription("test description");
        category.setExpenditureType("test type");
        // when/then
        assertTrue(validator.areAllFieldsCompleted(category));
    }
    
    @Test
    public void areAllFieldsCompletedShouldReturnFalse() {
        assertFalse(validator.areAllFieldsCompleted(category));
    }
    
    @Test
    public void validateExpenditureTypeShouldReturnError() {
        // when
        redirectAttributes = validator.validateExpenditureType(category, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }

    @Test
    public void validateExpenditureTypeShouldNotReturnError() {
        // given
        category.setDescription("test description");
        category.setExpenditureType("test type");
        // when
        redirectAttributes = validator.validateExpenditureType(category, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }
    
    @Test
    public void validateDescriptionShouldReturnError() {
        // when
        redirectAttributes = validator.validateDescription(category, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    public void validateDescriptionShouldNotReturnError() {
        // given
        category.setDescription("test description");
        // when
        redirectAttributes = validator.validateDescription(category, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
    }    
}