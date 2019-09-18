package com.personalbudget.demo.debt.dao;

import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import com.personalbudget.demo.debt.entity.Debt;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import org.hibernate.Session;
import org.hibernate.query.Query;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.dao.EmptyResultDataAccessException;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@SpringBootTest
public class DebtDAOImplTest {

    @Autowired
    private DebtDAO debtDAO;
    
    @Autowired
    EntityManager entityManager;
    
    @Test
    @Transactional
    @WithMockUser(username="a")
    public void getDebtsShouldReturnEmptyList() {
        assertThat(debtDAO.getDebts(), is(empty()));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getDebtsShouldReturnNonEmptyList() {
        assertThat(debtDAO.getDebts(), is(not(empty())));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getOnlyDebtsShouldNotReturnAnyClaims() {
        // given
        boolean areThereAnyClaims = false;
        // when
        List<Debt> list = debtDAO.getOnlyDebts();
        // then
        for (Debt item : list) {
            if (item.getType().equals("claim")) {
                areThereAnyClaims = true;
            }
        }
        assertFalse(areThereAnyClaims);
    }

    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getOnlyClaimsShouldNotReturnAnyDebts() {
        // given
        boolean areThereAnyDebts = false;
        // when
        List<Debt> list = debtDAO.getOnlyClaims();
        // then
        for (Debt item : list) {
            if (item.getType().equals("debt")) {
                areThereAnyDebts = true;
            }
        }
        assertFalse(areThereAnyDebts);
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getDebtShouldNotReturnNull() {
        assertNotNull(debtDAO.getDebt("Test debt"));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getDebtShouldReturnNull() {
        assertNull(debtDAO.getDebt("wrong debt name"));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getDebtIdShouldReturnIntGreaterThanZero() {
        assertThat(debtDAO.getDebtId("Test debt"), greaterThan(0));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getDebtIdShouldThrowEmptyResultDataAccessException() {
        // given
        boolean flag;
        // when
        try {
            debtDAO.getDebtId("wrong debt name");
            flag = false;
        }
        catch (EmptyResultDataAccessException ex) {
            flag = true;
        }
        // then
        assertTrue(flag);
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getDebtByIdShouldNotReturnNull() {
        assertNotNull(debtDAO.getDebtById(1));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void getDebtByIdShouldReturnNull() {
        assertNull(debtDAO.getDebtById(50));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void saveDebtTest() {
        // given
        Session session = entityManager.unwrap(Session.class);
        Query<String> query = session.createQuery("select debtName from debts where debtId=(select max(debtId) from debts)", String.class);
        String debtName = "saveDebtTest";
        String checkName;
        Debt debt = new Debt();
        debt.setAmount(15);
        debt.setCreditor("franek");
        debt.setCurrency("PLN");
        debt.setDebtName(debtName);
        debt.setType("debt");
        // when
        debtDAO.saveDebt(debt);
        // then
        try {
            checkName = query.getSingleResult();
        }
        catch (NoResultException ex) {
            checkName = null;
        }
        assertThat(debtName, equalTo(checkName));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void deleteDebtTest() {
        // given
        Session session = entityManager.unwrap(Session.class);
        Query<String> query = session.createQuery("select debtName from debts where debtId='1'", String.class);
        String checkName;
        // when
        debtDAO.deleteDebt(1);
        // then
        try {
            checkName = query.getSingleResult();
        }
        catch (NoResultException ex) {
            checkName = null;
        }
        assertThat(checkName, is(nullValue()));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void checkIfDebtExistsShouldReturnTrue() {
        assertTrue(debtDAO.checkIfDebtExists("Test debt"));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void checkIfDebtExistsShouldReturnFalse() {
        assertFalse(debtDAO.checkIfDebtExists("non-existing debt name"));
    }
    
    @Test
    @Transactional
    @WithMockUser(username="test")
    public void updateDebtTest() {
        // given
        Session session = entityManager.unwrap(Session.class);
        Query<Debt> query = session.createQuery("from debts where debtId='1'", Debt.class);
        Query<String> queryCheck = session.createQuery("select debtName from debts where debtId='1'", String.class);
        Debt debt;
        String debtName = "fooboo";
        String checkName;
        // when
        try {
            debt = query.getSingleResult();
        }
        catch (NoResultException ex) {
            debt = null;
        }
        debt.setDebtName(debtName);
        debtDAO.updateDebt(debt);
        // then
        try {
            checkName = queryCheck.getSingleResult();
        }
        catch (NoResultException ex) {
            checkName = null;
        }
        assertThat(debtName, equalTo(checkName));
    }
}