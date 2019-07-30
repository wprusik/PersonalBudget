package com.personalbudget.demo.transaction.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class TransactionSearch {

    String description;
    
    String fromAccount;
    
    String toAccount;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDate;
    
    String currency;
    
    String isPlanned;
    
    public TransactionSearch() {
        
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIsPlanned() {
        return isPlanned;
    }

    public void setIsPlanned(String isPlanned) {
        this.isPlanned = isPlanned;
    }
}
