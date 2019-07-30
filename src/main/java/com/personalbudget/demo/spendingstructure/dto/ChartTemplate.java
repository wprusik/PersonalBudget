package com.personalbudget.demo.spendingstructure.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class ChartTemplate {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDate;
    
    private String account;
    
    private String expType;

    public ChartTemplate() {
    }

    public ChartTemplate(Date startDate, Date endDate, String account, String expType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.account = account;
        this.expType = expType;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getExpType() {
        return expType;
    }

    public void setExpType(String expType) {
        this.expType = expType;
    }
    
}
