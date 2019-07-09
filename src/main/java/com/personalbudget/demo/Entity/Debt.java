package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="debts")
@Table(name="debts")
public class Debt {

    @Id
    @Column(name="debt_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int debtId;
    
    @Column(name="debt_name")
    private String debtName;
    
    @Column(name="creditor")
    private String creditor;
    
    @Column(name="type")
    private String type;
    
    @Column(name="amount")
    private float amount;
    
    @Column(name="username")
    private String username;
    
    @Column(name="currency")
    private String currency;

    public Debt() {
    }

    public String getDebtName() {
        return debtName;
    }

    public void setDebtName(String debtName) {
        this.debtName = debtName;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor = creditor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDebtId() {
        return debtId;
    }

    public void setDebtId(int debtId) {
        this.debtId = debtId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
