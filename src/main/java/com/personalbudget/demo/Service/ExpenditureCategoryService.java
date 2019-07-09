package com.personalbudget.demo.Service;

import com.personalbudget.demo.Entity.ExpenditureCategory;
import java.util.List;


public interface ExpenditureCategoryService {

    public List<ExpenditureCategory> getExpenditureCategories();
    
    public void saveExpenditureCategory(ExpenditureCategory expenditureCategory);
    
    public void deleteExpenditureCategory(String expenditureType);    
    
    public boolean isUnique(String expenditureType);
    
}
