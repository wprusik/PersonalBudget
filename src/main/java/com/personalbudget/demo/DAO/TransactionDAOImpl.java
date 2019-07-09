package com.personalbudget.demo.DAO;

import com.personalbudget.demo.Entity.Transaction;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDAOImpl implements TransactionDAO {

    private EntityManager entityManager;
    
    @Autowired
    public TransactionDAOImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }
    
    @Override
    public List<Transaction> getTransactions() {
        Session currentSession = entityManager.unwrap(Session.class);        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();        
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        
        Query<Transaction> query = currentSession.createQuery("from transactions where (datetime<='" + time + "' and username='" + username + "') order by date(datetime) DESC", Transaction.class);
        
        List<Transaction> tempList = (List<Transaction>) query.getResultList();
        
        return tempList;        
    }

    @Override
    public List<Transaction> getPlannedTransactions() {
        Session currentSession = entityManager.unwrap(Session.class);        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();        
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        
        Query<Transaction> query = currentSession.createQuery("from transactions where (datetime>'" + time + "' and username='" + username + "') order by date(datetime) ASC", Transaction.class);
        
        List<Transaction> tempList = (List<Transaction>) query.getResultList();
        
        return tempList;        
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        Session currentSession = entityManager.unwrap(Session.class);        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        transaction.setUsername(username);
        
        currentSession.save(transaction);
    }

    @Override
    public void deleteTransaction(int transactionId) {
        Session currentSession = entityManager.unwrap(Session.class);        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();
        
        Query<Transaction> query = currentSession.createQuery("from transactions where (username='" + username + "' and transaction_id='" + transactionId + "')", Transaction.class);
        
        Transaction transaction = query.getSingleResult();
        
        currentSession.delete(transaction);
    }

    @Override
    public Transaction getTransactionById(int id) {
        Session currentSession = entityManager.unwrap(Session.class);        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();        
        String username = auth.getName();        
        
        Query<Transaction> query = currentSession.createQuery("from transactions where (transaction_id='" + id + "' and username='" + username + "')", Transaction.class);
        
        Transaction transaction = (Transaction) query.getSingleResult();
        
        return transaction; 
    }
    
    @Override 
    public void updateTransaction(Transaction transaction)
    {
        Session currentSession = entityManager.unwrap(Session.class); 
        currentSession.saveOrUpdate(transaction);
    }
}
