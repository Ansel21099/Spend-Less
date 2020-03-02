package com.example.popularmovies;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies.database.FavoriteMovie;
import com.example.popularmovies.database.MovieDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavoriteMovie>> movies;

    public MainViewModel(Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<FavoriteMovie>> getMovies() {
        return movies;
    }
}
