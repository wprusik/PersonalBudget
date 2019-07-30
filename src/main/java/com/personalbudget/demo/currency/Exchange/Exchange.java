package com.personalbudget.demo.currency.exchange;

import java.util.Date;
import java.util.HashMap;

public class Exchange {

    private String base;
    
    private HashMap<String, Float> rates;
    
    private Date date; 
    
    public Exchange() {
        
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public HashMap<String, Float> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, Float> rates) {
        this.rates = rates;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    } 
    
}
