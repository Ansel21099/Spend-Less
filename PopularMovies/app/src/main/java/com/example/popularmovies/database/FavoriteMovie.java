package com.example.popularmovies.database;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="FavMovies")
public class FavoriteMovie {

    @PrimaryKey
    private int id;
    private String img_path;

    public FavoriteMovie(int id, String img_path) {
        this.id = id;
        this.img_path = img_path;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }




}

