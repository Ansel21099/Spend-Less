package com.example.spendless.model;

public class MBFeedback {
    String uid,feedback,rating;

    public MBFeedback(String uid, String feedback, String rating) {
        this.uid = uid;
        this.feedback = feedback;
        this.rating = rating;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
