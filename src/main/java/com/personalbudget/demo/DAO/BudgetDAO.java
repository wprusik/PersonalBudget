package com.personalbudget.demo.DAO;

import com.personalbudget.demo.Entity.Budget;
import java.util.List;


public interface BudgetDAO {

    public List<Budget> getBudgets();
    
    public Budget getBudgetById(int id);
    
    public void addBudget(Budget budget);
    
    public void removeBudget(int id);
    
    public void updateBudget(Budget budget);
        
}
