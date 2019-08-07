package com.personalbudget.demo.spendingstructure.logics;

import com.personalbudget.demo.spendingstructure.dto.ChartTemplate;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.expenditurecategory.entity.ExpenditureCategory;
import com.personalbudget.demo.transaction.logics.TransactionsManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
class SpendingStructureValidator {

    private TransactionsManager transactionsManager;
    
    @Autowired
    SpendingStructureValidator(TransactionsManager transactionsManager) {
        this.transactionsManager = transactionsManager;
    }
    
    
    RedirectAttributes validateFormCompletion(ChartTemplate template, RedirectAttributes redirectAttributes) {
        if ((template.getAccount() == null || template.getAccount().isBlank()) && (template.getExpType() == null || template.getExpType().isBlank())) {
            redirectAttributes.addFlashAttribute("error", "One of two fields (account or expenditure type) must be selected to generate a custom chart");            
        }
        return redirectAttributes;
    }
    
    boolean isStartDateSet(ChartTemplate template) {
        if (template.getStartDate() != null) {
            return true;
        }
        return false;
    }
    
    boolean isEndDateSet(ChartTemplate template) {
        if (template.getEndDate() != null) {
            return true;
        }
        return false;
    }
    
    RedirectAttributes validateTransactionList(List<Transaction> transactions, RedirectAttributes redirectAttributes) {
        if (transactions.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "There are no transactions matching the given criteria");
        }
        return redirectAttributes;
    }
    
    RedirectAttributes validateTimeRange(LocalDateTime start, LocalDateTime end, RedirectAttributes redirectAttributes) {
        long hours = ChronoUnit.HOURS.between(start, end);
        if (hours<2) {
            redirectAttributes.addFlashAttribute("error", "The time span between transactions is too small to generate a more detailed chart than those above");
        }
        return redirectAttributes;
    }
    
    boolean isAccountOptionSelected(ChartTemplate template) {
        if (template.getAccount() != null) {
            return true;
        }
        return false;
    }
    
    boolean doesTransactionMatchAccountAndExpenditureCategory(Transaction transaction, Account account, ExpenditureCategory expCat) {
        if (transaction.getType() != null && transaction.getExpenditureType() != null && transaction.getCurrency() != null) {
            if (transaction.getType().equals("outgoing") && transaction.getExpenditureType().equals(expCat.getExpenditureType()) && transaction.getAccountNumberFrom().equals(account.getAccountNumber())) {
                return true;
            }
        }
        return false;
    }
    
    boolean doesTransactionmatchExpenditureCategory(Transaction transaction, ExpenditureCategory expCat) {
        if (transaction.getExpenditureType() != null && transaction.getType() != null) {
            if (transaction.getType().equals("outgoing") && transaction.getExpenditureType().equals(expCat.getExpenditureType())) {
                return true;
            }
        }
        return false;
    }
}
