package com.personalbudget.demo.Service;

import com.personalbudget.demo.DAO.ExpenditureDAO;
import com.personalbudget.demo.Entity.Expenditure;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenditureServiceImpl implements ExpenditureService {

    @Autowired
    ExpenditureDAO expenditureDAO;
    
    
    public ExpenditureServiceImpl() {
        
    }    
    
    @Override
    @Transactional
    public void addExpenditure(Expenditure expenditure) {
        expenditureDAO.addExpenditure(expenditure);
    }

    @Override
    @Transactional
    public void removeExpenditure(int id) {
        expenditureDAO.removeExpenditure(id);
    }

    @Override
    @Transactional
    public Expenditure getExpenditureById(int id) {
        return expenditureDAO.getExpenditureById(id);
    }

    @Override
    @Transactional
    public List<Expenditure> getExpenditures(int budgetId) {
        return expenditureDAO.getExpenditures(budgetId);
    }

    @Override
    @Transactional
    public void updateExpenditures(List<Expenditure> expenditures) {
        expenditureDAO.updateExpenditures(expenditures);
    }

}
