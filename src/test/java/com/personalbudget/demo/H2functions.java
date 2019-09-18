package com.personalbudget.demo;

public class H2functions {

    public static String Date(String ldt) {
        String year = ldt.substring(0,4);
        String month = ldt.substring(8,10);
        String day = ldt.substring(5,7);
        ldt = year + "-" + month + "-" + day;
        return ldt;
    }
    
}
