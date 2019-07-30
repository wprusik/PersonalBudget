package com.personalbudget.demo.currency.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name="currency")
@Table(name="currency")
@Getter @Setter
public class Currency {
    
    @Id
    @Column(name="currency")
    private String currency;
    
    @Column(name="name")
    private String name;
        
    public Currency() {
        
    }
}
