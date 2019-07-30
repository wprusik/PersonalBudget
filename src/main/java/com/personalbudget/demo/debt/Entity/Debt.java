package com.personalbudget.demo.debt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name="debts")
@Table(name="debts")
@Getter @Setter
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
}
