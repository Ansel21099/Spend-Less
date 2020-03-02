package com.example.popularmovies;

import java.io.Serializable;

public class MoviesClass implements Serializable {

    private String id;


    /**
     * No args constructor for use in serialization
     */
    public MoviesClass() {
    }

    public MoviesClass(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
