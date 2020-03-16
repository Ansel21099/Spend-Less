package com.example.spendless.model;

public class MBCreateAccount {
    String name,email,currency;
    Double rating,totalincome,totalexpense;

    public MBCreateAccount(String name, String email, String currency, double rating, double totalincome, double totalexpense) {
        this.name = name;
        this.email = email;
        this.currency = currency;
        this.rating = rating;
        this.totalincome = totalincome;
        this.totalexpense = totalexpense;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getTotalincome() {
        return totalincome;
    }

    public void setTotalincome(double totalincome) {
        this.totalincome = totalincome;
    }

    public double getTotalexpense() {
        return totalexpense;
    }

    public void setTotalexpense(double totalexpense) {
        this.totalexpense = totalexpense;
    }
}
