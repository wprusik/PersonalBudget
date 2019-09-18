package com.personalbudget.demo.transaction.service;

import com.personalbudget.demo.transaction.dao.TransactionDAO;
import com.personalbudget.demo.transaction.entity.Transaction;
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
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
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
