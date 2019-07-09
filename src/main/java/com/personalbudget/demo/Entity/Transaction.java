package com.personalbudget.demo.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;

@Entity(name="transactions")
@Table(name="transactions")
public class Transaction {

    @Id
    @Column(name="transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;
    
    @Column(name="username")
    private String username;
    
    @Column(name="account_number_from")
    private String accountNumberFrom;
    
    @Column(name="account_number_to")
    private String accountNumberTo;
    
    @Column(name="amount")
    private float amount;
    
    @Column(name="datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;
    
    @Column(name="description")
    private String description;
    
    @Column(name="expenditure_type")
    private String expenditureType;
    
    @Column(name="debt_id")
    private int debtId;
    
    @Column(name="currency")
    private String currency;
    
    @Column(name="type")
    private String type;
    
    @Transient
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    
    @Transient
    private String ifIsDebtRepayment;
    
    
    public Transaction() {
        
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountNumberFrom() {
        return accountNumberFrom;
    }

    public void setAccountNumberFrom(String accountNumberFrom) {
        this.accountNumberFrom = accountNumberFrom;
    }

    public String getAccountNumberTo() {
        return accountNumberTo;
    }

    public void setAccountNumberTo(String accountNumberTo) {
        this.accountNumberTo = accountNumberTo;
    }
    
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpenditureType() {
        return expenditureType;
    }

    public void setExpenditureType(String expenditureType) {
        this.expenditureType = expenditureType;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getIfIsDebtRepayment() {
        return ifIsDebtRepayment;
    }

    public void setIfIsDebtRepayment(String ifIsDebtRepayment) {
        this.ifIsDebtRepayment = ifIsDebtRepayment;
    }
    
}
