package com.personalbudget.demo.debt.dao;

import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.security.SecurityService;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DebtDAOImpl implements DebtDAO {

    
    private EntityManager entityManager;
    private SecurityService securityService;
    
    @Autowired
    public DebtDAOImpl(EntityManager entityManager, SecurityService securityService) {
        this.entityManager = entityManager;
        this.securityService = securityService;
    }
    
    @Override
    public List<Debt> getDebts() {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();        
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE username='" + username + "'", Debt.class);
        List<Debt> debts = (List<Debt>) theQuery.getResultList();        
        return debts;
    }

    @Override
    public List<Debt> getOnlyDebts() {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();        
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE (username='" + username + "' AND type='debt')", Debt.class);
        List<Debt> debts = (List<Debt>) theQuery.getResultList();   
        return debts;
    }

    @Override
    public List<Debt> getOnlyClaims() {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();        
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE (username='" + username + "' AND type='claim')", Debt.class);
        List<Debt> debts = (List<Debt>) theQuery.getResultList();   
        return debts;
    }
    
    @Override
    public Debt getDebt(String name) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();        
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE username='" + username + "' AND debt_name='" + name + "'", Debt.class);
        Debt debt;
        try {
            debt = (Debt) theQuery.getSingleResult();
        }   
        catch (NoResultException ex) {
            debt = null;
        }
        return debt;
    }

    @Override
    public int getDebtId(String name) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();        
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE username='" + username + "' AND debt_name='" + name + "'", Debt.class);
        Debt debt = (Debt) theQuery.getSingleResult();
        return debt.getDebtId();
    }

    @Override
    public Debt getDebtById(int id) {
        Session currentSession = entityManager.unwrap(Session.class);                
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE debt_id='" + id + "'", Debt.class);
        Debt debt;
        try { 
            debt = (Debt) theQuery.getSingleResult();
        }        
        catch (NoResultException ex) {
            debt = null;
        }
        return debt;
    }

    @Override
    public void saveDebt(Debt theDebt) {
        Session currentSession = entityManager.unwrap(Session.class);  
        String username = securityService.getUsernameFromSecurityContext();
        theDebt.setUsername(username);        
        currentSession.save(theDebt);        
    }

    @Override
    public void deleteDebt(int id) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();     
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE (debt_id='" + id + "' and username='" + username + "')", Debt.class);
        Debt debt = (Debt) theQuery.getSingleResult();
        
        currentSession.delete(debt);
    }

    @Override
    public boolean checkIfDebtExists(String debtName) {
        Session currentSession = entityManager.unwrap(Session.class);
        String username = securityService.getUsernameFromSecurityContext();        
        Query<Debt> theQuery = currentSession.createQuery("from debts WHERE username='" + username + "' AND debt_name='" + debtName + "'", Debt.class);        
        List<Debt> debts = (List<Debt>) theQuery.getResultList();
        
        if (debts.isEmpty())
            return false;

        return true;
    }

    @Override
    public void updateDebt(Debt theDebt) {
        Session currentSession = entityManager.unwrap(Session.class);        
        currentSession.saveOrUpdate(theDebt);
    }
}
