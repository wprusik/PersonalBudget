package com.personalbudget.demo.DAO;

import com.personalbudget.demo.Entity.Transaction;
import java.util.List;

public interface TransactionDAO {
    
    public List<Transaction> getTransactions();
    
    public List<Transaction> getPlannedTransactions();
    
    public void saveTransaction(Transaction transaction);
    
    public void updateTransaction(Transaction transaction);
    
    public void deleteTransaction(int transactionId);
    
    public Transaction getTransactionById(int id);
}
