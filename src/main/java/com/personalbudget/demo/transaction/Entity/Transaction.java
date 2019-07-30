package com.personalbudget.demo.transaction.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity(name="transactions")
@Table(name="transactions")
@Getter @Setter
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
}
