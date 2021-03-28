package com.aleksandra.go4mytrip;

public class CostEstimateModel {
    String Id;
    String Title;
    String Date;
    int Amount;

    public CostEstimateModel() {
    }

    public CostEstimateModel(String id, String title, String date, int amount) {
        this.Id = id;
        this.Title = title;
        this.Date = date;
        this.Amount = amount;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }
}
