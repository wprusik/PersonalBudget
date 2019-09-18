package com.personalbudget.demo.expenditurecategory.logics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hibernate.Session;
import org.hibernate.query.Query;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@WithMockUser(username="test")
@SpringBootTest
public class ExpenditureCategoryManagerTest {

    @Autowired
    private ExpenditureCategoryManager manager;
    
    @Autowired
    private EntityManager entityManager;
    
    private RedirectAttributes redirectAttributes;
    private ExpenditureCategory category;
    
    @BeforeEach
    private void prepareMethod() {
        redirectAttributes = new RedirectAttributesModelMap();
        category = new ExpenditureCategory();
        category.setExpenditureType("");
        category.setDescription("");
    }
    
    @Test
    @Transactional
    public void addCategoryShouldReturnError() {
        // when
        redirectAttributes = manager.addCategory(category, redirectAttributes);
        // then
        assertThat(redirectAttributes.getFlashAttributes().containsKey("error"), is(true));
    }
    
    @Test
    @Transactional
    public void addCategoryShouldSaveCategoryAndReturnSuccess() {
        // given
        String description = "addCategory - test description";
        String type = "non-existing type";
        category.setDescription(description);
        category.setExpenditureType(type);
        Session session = entityManager.unwrap(Session.class);
        Query<ExpenditureCategory> query = session.createQuery("from expenditure_categories where expenditure_type='" + type + "'", ExpenditureCategory.class);
        ExpenditureCategory categoryCheck;
        // when
        redirectAttributes = manager.addCategory(category, redirectAttributes);
        // then
        try {
            categoryCheck = query.getSingleResult();
        }
        catch (NoResultException ex) {
            categoryCheck = null;
        }        
        assertThat("category have not been saved in the database", categoryCheck, is(not(nullValue())));
        assertThat("returned error: '" + redirectAttributes.getFlashAttributes().get("error") + "'", redirectAttributes.getFlashAttributes().containsKey("error"), is(false));
        assertThat("have not returned 'success' attribute", redirectAttributes.getFlashAttributes().containsKey("success"), is(true));        
    }
    
    @Test
    @Transactional
    public void deleteExpenditureTypeShouldRemoveCategoryAndReturnSuccess() {
        // given
        String type = "Testowanie";
        Session session = entityManager.unwrap(Session.class);
        Query<ExpenditureCategory> query = session.createQuery("from expenditure_categories where expenditure_type='" + type + "'", ExpenditureCategory.class);
        ExpenditureCategory categoryCheck;
        // when
        redirectAttributes = manager.deleteExpenditureType(type, redirectAttributes);
        // then        
        try {
            categoryCheck = query.getSingleResult();
        }
        catch (NoResultException ex) {
            categoryCheck = null;
        }
        assertThat("category have not been removed from database", categoryCheck, is(nullValue()));
        assertThat("have not returned 'success' attribute", redirectAttributes.containsAttribute("success"), is(true));
    }
}