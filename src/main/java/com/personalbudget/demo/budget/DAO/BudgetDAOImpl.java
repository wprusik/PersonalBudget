package com.personalbudget.demo.budget.dao;

import com.personalbudget.demo.budget.entity.Budget;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.security.core.context.SecurityContextHolder;

@Repository
public class BudgetDAOImpl implements BudgetDAO {
    
    private EntityManager entityManager;    

    @Autowired
    public BudgetDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public List<Budget> getBudgets() {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();        
        Query<Budget> query = currentSession.createQuery("from budgets where username='" + username + "'", Budget.class);        
        List<Budget> budgets = (List<Budget>) query.getResultList();        
        return budgets;
    }

    @Override
    public Budget getBudgetById(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Query<Budget> query = currentSession.createQuery("from budgets where (id='" + id + "' and username='" + username + "')", Budget.class);        
        try {
            Budget budget = query.getSingleResult();
            return budget;
        }
        catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void addBudget(Budget budget) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        budget.setUsername(username);
        currentSession.save(budget);        
    }

    @Override
    public void removeBudget(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Query<Budget> query = currentSession.createQuery("from budgets where (id='" + id + "' and username='" + username + "')", Budget.class);
        Budget budget = query.getSingleResult();
        currentSession.delete(budget);
    }

    @Override
    public void updateBudget(Budget budget) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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
