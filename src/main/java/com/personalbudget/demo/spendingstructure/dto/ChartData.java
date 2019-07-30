package com.personalbudget.demo.spendingstructure.dto;

import java.util.List;

public class ChartData {

    private String description;
    private float amount;
    private int numberOfTransactions;
    private String expType;
    private String name;
    private List<Float> amountList;

    public ChartData() {
    }

    public ChartData(String description, float amount, int numberOfTransactions, String expType) {
        this.description = description;
        this.amount = amount;
        this.numberOfTransactions = numberOfTransactions;
        this.expType = expType;
    }

    public ChartData(String name, List<Float> amountList) {
        this.name = name;
        this.amountList = amountList;
    }        

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public String getExpType() {
        return expType;
    }

    public void setExpType(String expType) {
        this.expType = expType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Float> getAmountList() {
        return amountList;
    }

    public void setAmountList(List<Float> amountList) {
        this.amountList = amountList;
    }
}
