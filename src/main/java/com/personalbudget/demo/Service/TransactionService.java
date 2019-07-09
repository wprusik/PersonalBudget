package com.personalbudget.demo.Service;

import com.personalbudget.demo.Entity.Transaction;
import java.util.List;

public interface TransactionService {
    
    public List<Transaction> getTransactions();
    
    public List<Transaction> getPlannedTransactions();
    
    public void saveTransaction(Transaction transaction);
    
    public void deleteTransaction(int transactionId);
    
    public Transaction getTransactionById(int id);
    
    public void updateTransaction(Transaction transaction);

}
