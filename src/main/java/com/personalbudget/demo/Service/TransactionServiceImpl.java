package com.personalbudget.demo.Service;

import com.personalbudget.demo.DAO.TransactionDAO;
import com.personalbudget.demo.Entity.Transaction;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    
    @Autowired
    private TransactionDAO transactionDAO;
    
    public TransactionServiceImpl() {
        
    }
    
    @Override
    @Transactional
    public List<Transaction> getTransactions() {
        return transactionDAO.getTransactions();
    }

    @Override
    @Transactional
    public List<Transaction> getPlannedTransactions() {
        return transactionDAO.getPlannedTransactions();
    }

    @Override
    @Transactional
    public void saveTransaction(Transaction transaction) {
        transactionDAO.saveTransaction(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(int transactionId) {
        transactionDAO.deleteTransaction(transactionId);
    }

    @Override
    @Transactional
    public Transaction getTransactionById(int id) {
        return transactionDAO.getTransactionById(id);
    }

    @Override
    @Transactional
    public void updateTransaction(Transaction transaction) {
        transactionDAO.updateTransaction(transaction);
    }

}
