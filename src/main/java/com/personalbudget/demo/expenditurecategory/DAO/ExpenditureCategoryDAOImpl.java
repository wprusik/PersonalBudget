package com.personalbudget.demo.expenditurecategory.dao;

import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.personalbudget.demo.security.SecurityManager;

@Repository
public class ExpenditureCategoryDAOImpl implements ExpenditureCategoryDAO {
    
    EntityManager entityManager;
    private SecurityManager securityManager;
    
    @Autowired
    public ExpenditureCategoryDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }
    
    @Override
    public List<ExpenditureCategory> getExpenditureCategories() {
        Session currentSession = entityManager.unwrap(Session.class);        
        String username = securityManager.getUsernameFromSecurityContext();        
        Query<ExpenditureCategory> query = currentSession.createQuery("from expenditure_categories where username='" + username + "'", ExpenditureCategory.class);        
        List<ExpenditureCategory> tempList = (List<ExpenditureCategory>) query.getResultList();        
        return tempList;
    }

    @Override
    public void saveExpenditureCategory(ExpenditureCategory expenditureCategory) {
        Session currentSession = entityManager.unwrap(Session.class);  
        currentSession.save(expenditureCategory);
    }

    @Override
    public void deleteExpenditureCategory(String expenditureType) {
        Session currentSession = entityManager.unwrap(Session.class);        
        String username = securityManager.getUsernameFromSecurityContext();        
        Query<ExpenditureCategory> query = currentSession.createQuery("from expenditure_categories where (username='" + username + "' and expenditure_type='" + expenditureType + "')", ExpenditureCategory.class);        
        ExpenditureCategory expenditureCategory = (ExpenditureCategory) query.getSingleResult();        
        currentSession.delete(expenditureCategory);
    }

    @Override
    public boolean isUnique(String expenditureType) {
        Session currentSession = entityManager.unwrap(Session.class);        
        String username = securityManager.getUsernameFromSecurityContext();      
        Query<ExpenditureCategory> query = currentSession.createQuery("from expenditure_categories where username='" + username + "'", ExpenditureCategory.class);        
        List<ExpenditureCategory> tempList = (List<ExpenditureCategory>) query.getResultList();
        
        for (ExpenditureCategory item : tempList)
            if (item.getExpenditureType().equals(expenditureType))
                return false;
        
        return true;        
    }

}