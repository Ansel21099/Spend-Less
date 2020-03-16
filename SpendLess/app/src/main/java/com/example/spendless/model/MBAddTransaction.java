package com.example.spendless.model;

public class MBAddTransaction {
    String category,date,description,type;
    Double amount;

    public MBAddTransaction(String category, String date, String description, String type, Double amount) {
        this.category = category;
        this.date = date;
        this.description = description;
        this.type = type;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
