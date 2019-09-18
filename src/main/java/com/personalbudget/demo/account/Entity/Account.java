package com.personalbudget.demo.account.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Setter;
import lombok.Getter;

@Entity(name="accounts")
@Table(name="accounts")
@Getter @Setter
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
}
