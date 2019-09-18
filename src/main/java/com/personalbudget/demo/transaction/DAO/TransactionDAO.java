package com.personalbudget.demo.transaction.dao;

import com.personalbudget.demo.transaction.entity.Transaction;
import java.util.List;

public interface TransactionDAO {
    
    public List<Transaction> getTransactions();
    
    public List<Transaction> getPlannedTransactions();
    
    public List<Transaction> getAllTransactions();
    
    public void saveTransaction(Transaction transaction);
    
    public void updateTransaction(Transaction transaction);
    
    public void deleteTransaction(int transactionId);
    
    public Transaction getTransactionById(int id);
}
