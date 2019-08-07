package com.personalbudget.demo.currency.exchange;

import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Exchange {

    private String base;
    
    private HashMap<String, Float> rates;
    
    private Date date; 
    
    public Exchange() {
        
    }
}
