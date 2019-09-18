package com.personalbudget.demo.budget.logics;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.budget.entity.Budget;
import com.personalbudget.demo.budget.entity.Expenditure;
import javax.persistence.NoResultException;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.personalbudget.demo.currency.entity.Currency;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WithMockUser(username="test")
@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class BudgetManagerTest {
    
    @Autowired
    private BudgetManager manager;
    
    @Autowired
    private EntityManager entityManager;
    
    private Session session;
    private RedirectAttributes redirectAttributes;
    
    @BeforeEach
    private void prepareMethod() {
        session = entityManager.unwrap(Session.class);
        redirectAttributes = new RedirectAttributesModelMap();
    }
    
    @Test
    @Transactional
    public void deleteExpenditureTest() {
        // given
        int expId = 1;
        int budgetId = 1;
        Expenditure expenditure;
        Query<Expenditure> query = session.createQuery("from expenditures where id='" + expId + "'", Expenditure.class);
        // when
        redirectAttributes = manager.deleteExpenditure(expId, budgetId, redirectAttributes);
        // then
        try {
            expenditure = query.getSingleResult();
        }
        catch (NoResultException ex) {
            expenditure = null;
        }
        assertNull(expenditure, "Expenditure has not been removed from database");
        assertThat( "Success redirect attribute has not been passed", redirectAttributes.getFlashAttributes().containsKey("success"), is(true));
    }
    
    @Test
    @Transactional
    public void getSumOfAllExpendituresShouldReturnProperSum() {
        assertThat(manager.getSumOfAllExpenditures(1), comparesEqualTo((float) 201));
    }
    
    @Test
    @Transactional
    public void getSumOfAllExpendituresShouldReturnZero() {
        assertThat(manager.getSumOfAllExpenditures(3), comparesEqualTo((float) 0));
    }
    
    @Test
    @Transactional
    public void getCurrencyOfBudgetTest() {
        // given
        Currency currency;
        Query<String> query = session.createQuery("select currency from budgets where budget_id=1", String.class);
        String currencyCheck = query.getSingleResult();
        // when
        currency = manager.getCurrencyOfBudget(1);
        // then
        assertEquals(currency.getCurrency(), currencyCheck);
    }
    
    @Test
    @Transactional
    public void addExpenditureTest() {
        // given        
        String description = "addExpenditureTest";        
        String descriptionFromDB;
        Expenditure expenditure = new Expenditure();
        expenditure.setDescription(description);
        expenditure.setAmount(5);
        expenditure.setBudgetId(1);
        Query<String> query = session.createQuery("select description from expenditures where id=(select max(id) from expenditures)", String.class);
        // when
        redirectAttributes = manager.addExpenditure(expenditure, redirectAttributes);
        // then
        descriptionFromDB = query.getSingleResult();
        assertEquals(descriptionFromDB, description);
    }

    @Test
    @Transactional
    public void addBudgetTest() {
        // given
        String budgetName = "addBudgetTest";
        String budgetNameCheck;
        Budget budget = new Budget();
        budget.setAmount(15);
        budget.setBudgetName(budgetName);
        budget.setCurrency("PLN");
        budget.setPurpose("for tests");
        budget.setBudgetId(4);
        Query<String> query = session.createQuery("select budgetName from budgets where budgetId='4'", String.class);
        // when
        redirectAttributes = manager.addBudget(budget, redirectAttributes);
        // then
        budgetNameCheck = query.getSingleResult();        
        assertEquals(budgetNameCheck, budgetName);
    }
    
    @Test
    @Transactional
    public void updateBudgetTest() {
        // given
        Query<Budget> query = session.createQuery("from budgets where budgetId='1'", Budget.class);
        Query<String> queryCheck = session.createQuery("select budgetName from budgets where budgetId='1'", String.class);
        Budget budget = query.getSingleResult();
        String testName = "updateBudgetTest";
        // when
        budget.setBudgetName(testName);
        manager.updateBudget(budget);
        // then
        assertEquals(testName, queryCheck.getSingleResult());
    }    
}