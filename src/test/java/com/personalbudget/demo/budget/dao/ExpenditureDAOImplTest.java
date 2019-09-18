package com.personalbudget.demo.budget.dao;

import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.personalbudget.demo.budget.entity.Expenditure;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class ExpenditureDAOImplTest {

    @Autowired
    private ExpenditureDAO expDAO;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    @Transactional
    public void addExpenditureTest() {
        // given
        Session currentSession = entityManager.unwrap(Session.class);
        String description = "addExpenditureTest";
        Query<Expenditure> query = currentSession.createQuery("FROM expenditures WHERE description='" + description + "'", Expenditure.class);
        Expenditure expenditure = new Expenditure();
        expenditure.setAmount(15);
        expenditure.setBudgetId(1);
        expenditure.setDescription(description);        
        // when
        expDAO.addExpenditure(expenditure);
        // then        
        try { 
            expenditure = query.getSingleResult();            
        }
        catch (NoResultException ex) {            
            expenditure = null;
        }
        assertThat(expenditure, is(not(nullValue())));
    }
    
    @Test
    @Transactional
    public void removeExpenditureTest() {
        // given
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Expenditure> query = currentSession.createQuery("from expenditures where id='1'", Expenditure.class);
        Expenditure expenditure;
        // when
        expDAO.removeExpenditure(1);
        // then        
        try { 
            expenditure = query.getSingleResult();            
        }
        catch (NoResultException ex) {            
            expenditure = null;
        }
        assertThat(expenditure, is(nullValue()));
    }
    
    @Test
    @Transactional
    public void getExpenditureByIdShouldReturnNull() {
        assertNull(expDAO.getExpenditureById(500));
    }
    
    @Test
    @Transactional
    public void getExpenditureByIdShouldNotReturnNull() {
        assertNotNull(expDAO.getExpenditureById(2));
    }
    
    @Test    
    @Transactional
    public void getExpendituresShouldReturnEmptyList() {
        assertThat(expDAO.getExpenditures(500), is(empty()));
    }
    
    @Test    
    @Transactional
    public void getExpendituresShouldReturnNonEmptyList() {
        assertThat(expDAO.getExpenditures(1), is(not(empty())));
    }
    
    @Test    
    @Transactional
    public void updateExpendituresTest() {
        // given
        Session currentSession = entityManager.unwrap(Session.class);        
        Query<Expenditure> query = currentSession.createQuery("from expenditures where budget_id='1'", Expenditure.class);
        List<Expenditure> expenditures = (List<Expenditure>) query.getResultList();
        String updatedDescription = "updateExpenditureTest";
        // when
        expenditures.get(0).setDescription(updatedDescription);
        expDAO.updateExpenditures(expenditures);
        expenditures = (List<Expenditure>) query.getResultList();
        // then
        assertEquals(expenditures.get(0).getDescription(), updatedDescription);
    }

}