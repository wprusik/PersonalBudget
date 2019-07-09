package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="budgets")
@Table(name="budgets")
public class Budget {

    @Id
    @Column(name="budget_id")
    private int budgetId;
    
    @Column(name="username")
    private String username;
    
    @Column(name="budget_name")
    private String budgetName;
    
    @Column(name="purpose")
    private String purpose;
    
    @Column(name="amount")
    private float amount;
    
    @Column(name="currency")
    private String currency;
    

    
    public Budget() {
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
