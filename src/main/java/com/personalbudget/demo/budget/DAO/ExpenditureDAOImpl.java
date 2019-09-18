package com.personalbudget.demo.budget.dao;

import com.personalbudget.demo.budget.entity.Expenditure;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenditureDAOImpl implements ExpenditureDAO {

    private EntityManager entityManager;
    
    @Autowired
    public ExpenditureDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }
    
    @Override
    public void addExpenditure(Expenditure expenditure) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.save(expenditure);
    }

    @Override
    public void removeExpenditure(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Expenditure> query = currentSession.createQuery("from expenditures where id='" + id + "'", Expenditure.class);
        Expenditure exp = query.getSingleResult();
        currentSession.delete(exp);
    }

    @Override
    public Expenditure getExpenditureById(int id) {
        Session curreSession = entityManager.unwrap(Session.class);
        Query<Expenditure> query = curreSession.createQuery("from expenditures where id='" + id + "'", Expenditure.class);
        Expenditure exp;
        try {
            exp = query.getSingleResult();
        }
        catch (NoResultException ex) {
            exp = null;
        }
        return exp;
    }

    @Override
    public List<Expenditure> getExpenditures(int budgetId) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Expenditure> query = currentSession.createQuery("from expenditures where budget_id='" + budgetId + "'", Expenditure.class);
        List<Expenditure> expenditures = (List<Expenditure>) query.getResultList();
        return expenditures;
    }

    @Override
    public void updateExpenditures(List<Expenditure> expenditures) {
        Session currentSession = entityManager.unwrap(Session.class);            
        int budgetId = expenditures.get(0).getBudgetId();
        Query<Expenditure> query = currentSession.createQuery("from expenditures where budget_id='" + budgetId + "'", Expenditure.class);
        List<Expenditure> originalExpenditures = (List<Expenditure>) query.getResultList();
        
        for (Expenditure ori : originalExpenditures)
        {
            for (Expenditure item : expenditures)
            {
                if (ori.getId() == item.getId())
                {                    
                    ori.setAmount(item.getAmount());
                    break;
                }                
            }            
        }
        
        
        for(Expenditure item : originalExpenditures)
            currentSession.saveOrUpdate(item);        
    }
}
