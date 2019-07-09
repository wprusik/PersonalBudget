package com.personalbudget.demo.DAO;

import com.personalbudget.demo.Entity.Budget;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetDAOImpl implements BudgetDAO {

    @Autowired
    EntityManager entityManager;

    public BudgetDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }
    
    @Override
    public List<Budget> getBudgets() {
        Session currentSession = entityManager.unwrap(Session.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        Query<Budget> query = currentSession.createQuery("from budgets where username='" + username + "'", Budget.class);
        
        List<Budget> budgets = (List<Budget>) query.getResultList();
        
        return budgets;
    }

    @Override
    public Budget getBudgetById(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        Query<Budget> query = currentSession.createQuery("from budgets where (id='" + id + "' and username='" + username + "')", Budget.class);
        Budget budget = query.getSingleResult();
        
        return budget;
    }

    @Override
    public void addBudget(Budget budget) {
        Session currentSession = entityManager.unwrap(Session.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        budget.setUsername(username);
        currentSession.save(budget);        
    }

    @Override
    public void removeBudget(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Query<Budget> query = currentSession.createQuery("from budgets where (id='" + id + "' and username='" + username + "')", Budget.class);
        Budget budget = query.getSingleResult();
        currentSession.delete(budget);
    }

    @Override
    public void updateBudget(Budget budget) {
        Session currentSession = entityManager.unwrap(Session.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        int id = budget.getBudgetId();
        Query<Budget> query = currentSession.createQuery("from budgets where (id='" + id + "' and username='" + username + "')", Budget.class);
        Budget oriBudget = query.getSingleResult();
        
        oriBudget.setAmount(budget.getAmount());
        oriBudget.setBudgetName(budget.getBudgetName());
        oriBudget.setPurpose(budget.getPurpose());
        oriBudget.setCurrency(budget.getCurrency());
                
        currentSession.saveOrUpdate(oriBudget);
    }
}
