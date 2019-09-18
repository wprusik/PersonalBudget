package com.personalbudget.demo.debt.logics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.personalbudget.demo.debt.entity.Debt;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@TestPropertySource(locations="/application-test.properties")
@Sql({"/schema.sql", "/data.sql"})
@WithMockUser(username="test")
@SpringBootTest
public class DebtManagerTest {

    @Autowired
    private DebtManager manager;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    @Transactional
    public void addDebtTest() {
        // given
        Session session = entityManager.unwrap(Session.class);
        Query<String> query = session.createQuery("select debtName from debts where debtId=(select max(debtId) from debts)", String.class);
        String checkName;
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Debt debt = new Debt();
        debt.setAmount(15);
        debt.setCreditor("test creditor");
        debt.setCurrency("PLN");
        debt.setDebtName("test name");
        debt.setType("debt");
        // when
        redirectAttributes = manager.addDebt(debt, redirectAttributes);
        // then
        try {
            checkName = query.getSingleResult();
        }
        catch (NoResultException ex) {
            checkName = "not this time";
        }
        assertThat(checkName, equalTo(debt.getDebtName()));
    }    
}