package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.popularmovies.database.FavoriteMovie;
import com.example.popularmovies.database.MovieDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetails extends AppCompatActivity {

    Integer id;
    TextView title, user_rating, release_date, synopsis, tvreview;
    ImageView poster_image;
    ListView lv;
    TrailerAdapter adapter;
    ArrayList<ModelTrailer> arrayList = new ArrayList<ModelTrailer>();
    ModelTrailer model;
    ToggleButton tog;
    private MovieDatabase mDb;
    String rev,poster;
    Boolean flag=false;

    //Add your api key here
    String api_key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        title = findViewById(R.id.title);
        user_rating = findViewById(R.id.user_rating);
        release_date = findViewById(R.id.release_date);
        synopsis = findViewById(R.id.synopsis);
        poster_image = findViewById(R.id.poster_image);
        lv=findViewById(R.id.lvTrailers);
        tvreview = findViewById(R.id.tvreview);
        tog = findViewById(R.id.toggleFav);

        Intent intent = getIntent();
        id = intent.getIntExtra("Movie ID", 0);
        poster = intent.getStringExtra("Poster");

        mDb = MovieDatabase.getInstance(getApplicationContext());

        FetchMovieDetails fetchMovieDetails = new FetchMovieDetails();
        fetchMovieDetails.execute();

        Isfav is = new Isfav();
        is.execute();

    }

    public class Fav extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            final FavoriteMovie mov = new FavoriteMovie(id,poster);
            if(params[0].equals("false"))
            {
                mDb.movieDao().deleteMovie(mov);
            } else {
                mDb.movieDao().insertMovie(mov);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }

    public class Isfav extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            final FavoriteMovie fmov = mDb.movieDao().loadMovieById(id);
            if (fmov==null)
                flag=false;
            else
                flag=true;
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            tog.setChecked(flag);
            tog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Fav fa = new Fav();
                    fa.execute(""+isChecked);
                }
            });

        }
    }


    public class FetchMovieDetails extends AsyncTask<Void, Void, Void> {

        String LOG_TAG = "FetchMovieDetails";
        String original_title, releaseDate, plotSynopsis, poster_path;
        Double ratings;

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;
            String trailer = null;

            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + id + "?api_key=" + api_key);
                Log.d(LOG_TAG,"URL: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.d(LOG_TAG, "JSON Parsed: " + movieJsonStr);

                JSONObject main = new JSONObject(movieJsonStr);
                original_title = main.getString("original_title");
                releaseDate = main.getString("release_date");
                ratings = main.getDouble("vote_average");
                plotSynopsis = main.getString("overview");
                poster_path = "https://image.tmdb.org/t/p/w500" + main.getString("poster_path");

            }catch(Exception e){
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url1 = new URL(base_url + id + "/videos?api_key=" + api_key);
                Log.d(LOG_TAG,"URL: " + url1.toString());

                urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0) {
                    return null;
                }
                trailer = buffer.toString();
                Log.d(LOG_TAG, "JSON Parsed: " + trailer);

                JSONObject main = new JSONObject(trailer);
                JSONArray arr = main.getJSONArray("results");
                JSONObject trail;
                String name,key;
                arrayList.clear();
                for(int i =0; i < arr.length(); i++) {
                    trail = arr.getJSONObject(i);
                    name = trail.getString("name");
                    key = trail.getString("key");
                    model = new ModelTrailer(name,key);
                    arrayList.add(model);
                }

            }catch(Exception e){
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url1 = new URL(base_url + id + "/reviews?api_key=" + api_key);
                Log.d(LOG_TAG,"URL: " + url1.toString());

                urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if(buffer.length() == 0) {
                    return null;
                }
                trailer = buffer.toString();
                Log.d(LOG_TAG, "JSON Parsed: " + trailer);

                JSONObject main = new JSONObject(trailer);
                JSONArray arr = main.getJSONArray("results");
                JSONObject trail;
                String author,content;
                rev="Reviews : ";
                for(int i =0; i < arr.length(); i++) {
                    trail = arr.getJSONObject(i);
                    author = trail.getString("author");
                    content = trail.getString("content");
                    rev += "\n\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n\n" + content + "\n\n- " + author ;
                }

            }catch(Exception e){
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            getSupportActionBar().setTitle(original_title);
            title.setText(original_title);
            user_rating.setText("User Ratings: " +ratings + " / 10");
            release_date.setText("Release Date: " + releaseDate);
            synopsis.setText(plotSynopsis);
            poster_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            poster_image.setPadding(10, 10, 10, 10);
            Picasso.get().load(poster_path).into(poster_image);
            tvreview.setText(rev);
            adapter = new TrailerAdapter(getApplicationContext(), arrayList);
            try {
                lv.setAdapter(adapter);
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "Error", e);
            }
        }
    }
}
