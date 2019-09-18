package com.personalbudget.demo.budget.dao;

import javax.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.budget.entity.Budget;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import org.hibernate.Session;
import org.hibernate.query.Query;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.security.core.context.SecurityContextHolder;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class BudgetDAOImplTest {

    @Autowired
    BudgetDAO budgetDAO;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    @WithMockUser(username="oof")
    @Transactional
    public void getBudgetsShouldReturnEmptyList() {
        assertThat(budgetDAO.getBudgets(), is(empty()));
    }
    
    @Test
    @WithMockUser(username="test")
    @Transactional
    public void getBudgetsShouldReturnNonEmptyList() {        
        assertThat(budgetDAO.getBudgets(), is(not(empty())));
    }
    
    @Test
    @WithMockUser(username="test")
    @Transactional
    public void getBudgetByIdShouldReturnNull() {
        assertNull(budgetDAO.getBudgetById(200));
    }
    
    @Test
    @WithMockUser(username="test")
    @Transactional
    public void addBudgetTest() {
        // given
        Session currentSession = entityManager.unwrap(Session.class);
        String budgetName = "addBudgetTest";
        Query<Budget> query = currentSession.createQuery("FROM budgets WHERE budget_name='" + budgetName + "'", Budget.class);
        Budget budget = new Budget();
        Budget budgetFromDB;        
        budget.setAmount(15);
        budget.setBudgetName(budgetName);
        budget.setCurrency("PLN");
        budget.setPurpose("test purpose");
        budget.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        // when
        budgetDAO.addBudget(budget);        
        // then        
        try { 
            budgetFromDB = query.getSingleResult();            
        }
        catch (NoResultException ex) {            
            budgetFromDB = null;
        }
        assertNotNull(budgetFromDB);
    }
    
    @Test
    @WithMockUser(username="test")
    @Transactional
    public void removeBudgetTest() {
        // given
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Budget> query = currentSession.createQuery("from budgets where id='1'", Budget.class);
        Budget budget;
        // when
        budgetDAO.removeBudget(1);
        // then        
        try { 
            budget = query.getSingleResult();            
        }
        catch (Exception ex) {            
            budget = null;
        }
        assertNull(budget);      
    }
    
    @Test
    @WithMockUser(username="test")
    @Transactional
    public void updateBudgetTest() {
        // given
        Budget budget = budgetDAO.getBudgetById(1);
        String budgetName = "changed name";
        budget.setBudgetName(budgetName);
        // when
        budgetDAO.updateBudget(budget);
        // then
        assertEquals(budgetDAO.getBudgetById(1).getBudgetName(), budgetName);
    }
}