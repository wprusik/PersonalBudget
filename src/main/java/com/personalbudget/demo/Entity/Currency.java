package com.personalbudget.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="currency")
@Table(name="currency")
public class Currency {
    
    @Id
    @Column(name="currency")
    private String currency;
    
    @Column(name="name")
    private String name;
        
    public Currency() {
        
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
