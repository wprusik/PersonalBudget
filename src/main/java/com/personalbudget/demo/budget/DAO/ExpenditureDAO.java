package com.personalbudget.demo.budget.dao;

import com.personalbudget.demo.budget.entity.Expenditure;
import java.util.List;

public interface ExpenditureDAO {

    public void addExpenditure(Expenditure expenditure);
    
    public void removeExpenditure(int id);
    
    public Expenditure getExpenditureById(int id);
    
    public List<Expenditure> getExpenditures(int budgetId);
    
    public void updateExpenditures(List<Expenditure> expenditures);
    
}
