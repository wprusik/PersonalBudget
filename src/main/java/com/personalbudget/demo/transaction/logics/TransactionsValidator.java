package com.personalbudget.demo.transaction.logics;

import static com.personalbudget.demo.transaction.logics.TransactionsManager.formatDescription;
import static com.personalbudget.demo.transaction.logics.TransactionsManager.isDebtRepayment;
import com.personalbudget.demo.account.entity.Account;
import com.personalbudget.demo.account.service.AccountService;
import com.personalbudget.demo.transaction.entity.Transaction;
import com.personalbudget.demo.transaction.dto.TransactionSearch;
import com.personalbudget.demo.debt.service.DebtService;
import com.personalbudget.demo.debt.entity.Debt;
import com.personalbudget.demo.currency.exchange.ExchangeManager;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
class TransactionsValidator {

    private AccountService accountService;
    private DebtService debtService;
    
    @Autowired
    public TransactionsValidator(AccountService accountService, DebtService debtService) {
        this.accountService = accountService;
        this.debtService = debtService;
    }
    
    boolean isDebtRepaymentOptionSet(Transaction transaction) {
        if (transaction.getIfIsDebtRepayment() == null) {
            return false;
        }
        return true;
    }
    
    boolean isDateSet(Transaction transaction) {
        if (transaction.getDate() == null) {
            return false;
        }
        return true;
    }
    
    boolean isCurrencySet(Transaction transaction) {
        if (transaction.getCurrency() == null) {
            return false;
        }
        return true;
    }
    
    boolean doesRequireDescription(Transaction transaction) {
        String transactionType = transaction.getType();
        String[] consideredTypes = {"incoming", "outgoing", "between", "debt"};
        for (String item : consideredTypes) {
            if (item.equals(transactionType)) {
                return true;
            }
        }
        return false;
    }
    
    RedirectAttributes validateDescription(String description, RedirectAttributes redirectAttributes) {
        if (description.trim().replaceAll(" +", " ").length() < 4) {
            redirectAttributes.addFlashAttribute("error", "Enter the correct description.");
        }
        return redirectAttributes;
    }   
    
    boolean isTransactionTypeDebt(Transaction transaction) {
        if (transaction.getType().equals("debt")) {
            return true;
        }
        return false;
    }
    
    RedirectAttributes validateAmount(Transaction transaction, RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccount(transaction.getAccountNumberFrom());
        boolean isDebtRepayment = isDebtRepayment(transaction);
        if (account.getBalance() < transaction.getAmount()) {
            if (isDebtRepayment) {
                redirectAttributes.addFlashAttribute("error","Transaction has not been added. You don't have that much money on that account.");
            }
            else {
                redirectAttributes.addAttribute("error","lackOfMoney");
            }            
        }
        else if (isTransactionTypeDebt(transaction)) {            
            Debt debt = debtService.getDebtById(transaction.getDebtId());
            ExchangeManager exManager = new ExchangeManager();
            if (debt.getAmount() < (float) exManager.convertCurrency(debt.getCurrency(), transaction.getCurrency(), transaction.getAmount())) {
                if (isDebtRepayment) {
                    redirectAttributes.addFlashAttribute("error","The transaction has not been added. The debt is lower than the amount you tried to pay.");                        
                }
                else {
                    redirectAttributes.addAttribute("error","tooMuchMoney");
                }
            }
        }    
        return redirectAttributes;
    }
    
    boolean isSearchTemplateEmpty(TransactionSearch searchTemplate) {
        if (searchTemplate.getDescription().equals("") && searchTemplate.getFromAccount().equals("") && searchTemplate.getToAccount().equals("") && searchTemplate.getCurrency().equals("") && searchTemplate.getStartDate() == null & searchTemplate.getEndDate() == null) {
            return true;
        }
        return false;
    }
    
    boolean doesTransactionMatchCriteria(Transaction transaction, TransactionSearch searchTemplate) {
        if (isMatchingDescriptionField(transaction, searchTemplate)
                && isMatchingFromAccountField(transaction, searchTemplate)
                && isMatchingToAccountField(transaction, searchTemplate)
                && isMatchingCurrencyField(transaction, searchTemplate)
                && isMatchingDateFields(transaction, searchTemplate)
                ) {
            return true;
        }
        return false;
    }
    
    private boolean isMatchingDescriptionField(Transaction transaction, TransactionSearch searchTemplate) {
        if (searchTemplate.getDescription().equals("") || formatDescription(searchTemplate.getDescription()).equals(transaction.getDescription())) {
            return true;
        }
        return false;
    }
    
    private boolean isMatchingFromAccountField(Transaction transaction, TransactionSearch searchTemplate) {
        if (searchTemplate.getFromAccount().equals("") || searchTemplate.getFromAccount().equals(transaction.getAccountNumberFrom())) {
            return true;
        }
        return false;
    }
    
    private boolean isMatchingToAccountField(Transaction transaction, TransactionSearch searchTemplate) {
        if (searchTemplate.getToAccount().equals("") || searchTemplate.getToAccount() == null || searchTemplate.getToAccount().equals(transaction.getAccountNumberTo())) {
            return true;
        }
        return false;
    }
    
    private boolean isMatchingCurrencyField(Transaction transaction, TransactionSearch searchTemplate) {
        if (searchTemplate.getCurrency().equals("") || searchTemplate.getCurrency().equals(transaction.getCurrency())) {
            return true;
        }
        return false;
    }
    
    private boolean isMatchingDateFields(Transaction transaction, TransactionSearch searchTemplate) {
        if (isStartDateSet(searchTemplate)) {
            LocalDateTime start = LocalDateTime.ofInstant(searchTemplate.getStartDate().toInstant(), ZoneId.systemDefault());
            if (isEndDateSet(searchTemplate)) {
                LocalDateTime end = LocalDateTime.ofInstant(searchTemplate.getEndDate().toInstant(), ZoneId.systemDefault());
                if (transaction.getDateTime().isBefore(start) || transaction.getDateTime().isAfter(end)) {
                    return false;
                }
            }
            else {
                if (transaction.getDateTime().isBefore(start)) {
                    return false;
                }
            }        
        }
        else if (isEndDateSet(searchTemplate)) {
            LocalDateTime endLdf = LocalDateTime.ofInstant(searchTemplate.getEndDate().toInstant(), ZoneId.systemDefault());
            if (transaction.getDateTime().isAfter(endLdf))
                return false;
        }
        return true;
    }
    
    boolean isStartDateSet(TransactionSearch searchTemplate) {
        if (searchTemplate.getStartDate() != null) {
            return true;
        }
        return false;
    }
    
    boolean isEndDateSet(TransactionSearch searchTemplate) {
        if (searchTemplate.getEndDate()!= null) {
            return true;
        }
        return false;
    }
}
