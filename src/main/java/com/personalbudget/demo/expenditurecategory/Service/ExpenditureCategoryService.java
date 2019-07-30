package com.personalbudget.demo.expenditurecategory.service;

import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import java.util.List;


public interface ExpenditureCategoryService {

    public List<ExpenditureCategory> getExpenditureCategories();
    
    public void saveExpenditureCategory(ExpenditureCategory expenditureCategory);
    
    public void deleteExpenditureCategory(String expenditureType);    
    
    public boolean isUnique(String expenditureType);
    
}
