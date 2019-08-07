package com.personalbudget.demo.expenditurecategory.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name="expenditure_categories")
@Table(name="expenditure_categories")
@Getter @Setter
public class ExpenditureCategory {
    
    @Id
    @Column(name="expenditure_type")
    private String expenditureType;
    
    @Column(name="description")
    private String description;
    
    @Column(name="username")
    private String username;

    public ExpenditureCategory() {   
        
    }
}
