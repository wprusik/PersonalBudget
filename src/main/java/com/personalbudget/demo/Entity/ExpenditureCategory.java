package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="expenditure_categories")
@Table(name="expenditure_categories")
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

    public String getExpenditureType() {
        return expenditureType;
    }

    public void setExpenditureType(String expenditureType) {
        this.expenditureType = expenditureType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
