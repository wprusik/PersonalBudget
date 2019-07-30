package com.personalbudget.demo.budget.service;

import com.personalbudget.demo.budget.entity.Budget;
import java.util.List;

public interface BudgetService {

    public List<Budget> getBudgets();
    
    public Budget getBudgetById(int id);
    
    public void addBudget(Budget budget);
    
    public void removeBudget(int id);
    
    public void updateBudget(Budget budget);
}
