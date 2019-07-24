package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity(name="budgets")
@Table(name="budgets")
@Getter @Setter
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

}
