package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="accounts")
@Table(name="accounts")
public class Account {
    
    @Column(name="username")
    private String username;
    
    @Id
    @Column(name="account_number")
    private String accountNumber;
    
    @Column(name="bank")
    private String bank;
    
    @Column(name="balance")
    private float balance;
    
    @Column(name="currency")
    private String currency;
    
    @Column(name="account_name")
    private String accountName;
    
        
    public Account() {
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
   
}
