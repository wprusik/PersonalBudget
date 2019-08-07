package com.personalbudget.demo.budget.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Table(name="expenditures")
@Entity(name="expenditures")
@Getter @Setter
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @Column(name="budget_id")
    private int budgetId;
    
    @Column(name="amount")
    private float amount;
    
    @Column(name="description")
    private String description;

    @Transient
    private String edit;        
    
    public Expenditure() {
        
    }
    
    public Expenditure(int budgetId, String edit) {
        this.budgetId = budgetId;
        this.edit = edit;
    }
}
