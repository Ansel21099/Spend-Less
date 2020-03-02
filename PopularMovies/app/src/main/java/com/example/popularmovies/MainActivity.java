package com.example.popularmovies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

import com.example.popularmovies.database.FavoriteMovie;
import com.example.popularmovies.database.MovieDatabase;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ArrayList<String> posters;
    ArrayList<Integer> ids;

    String sort_type;
    ArrayList<ModelDataMain> arrayList = new ArrayList<ModelDataMain>();
    ModelDataMain model;
    GridView gridView;
    Adapter imageAdapter;
    FetchMovies fetchMovies;
    String image_url = "https://image.tmdb.org/t/p/w500";
    private List<FavoriteMovie> favMovs;

    //Insert API key here
    String api_key = "be2874b98bdb4541dfc391234954d61b";

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("popular_movies",MODE_PRIVATE);
        sort_type = sharedPreferences.getString("sort_type", "popular");

        if(sort_type.equals("popular"))
            getSupportActionBar().setTitle(R.string.popular);
        else if(sort_type.equals("top_rated"))
            getSupportActionBar().setTitle(R.string.top_rated);
        else if(sort_type.equals("favourite"))
            getSupportActionBar().setTitle(R.string.fav);

       gridView= findViewById(R.id.GvPoster);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetails.class);
                if(!sort_type.equals("favourite")) {
                    intent.putExtra("Movie ID", ids.get(position));
                    intent.putExtra("Poster", posters.get(position));
                }
                else                {
                    intent.putExtra("Movie ID", favMovs.get(position).getId());
                    intent.putExtra("Poster", favMovs.get(position).getImg_path());
                }
                startActivity(intent);
            }
        });

        favMovs = new ArrayList<FavoriteMovie>();
        fetchMovies = new FetchMovies();
        if(!sort_type.equals("favourite"))
            fetchMovies.execute(sort_type);
        else {
            setupViewModel();
        }
    }
    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteMovie> favs) {
                if(favs.size()>0) {
                    favMovs.clear();
                    favMovs = favs;
                }
                arrayList.clear();
                for (int i=0; i<favMovs.size(); i++) {
                    model = new ModelDataMain(image_url + favMovs.get(i).getImg_path());
                    arrayList.add(model);
                }
                imageAdapter = new Adapter(getApplicationContext(), arrayList);
                try {
                    gridView.setAdapter(imageAdapter);
                } catch (NullPointerException e) {

                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final SharedPreferences.Editor editor=sharedPreferences.edit();
            int selected = 0;
            sort_type = sharedPreferences.getString("sort_type", "popular");
            if(sort_type.equals("popular"))
                selected = 0;
            else if(sort_type.equals("top_rated"))
                selected = 1;
            else if(sort_type.equals("favourite"))
                selected = 2;
            builder.setTitle(R.string.dialog_title);
            builder.setSingleChoiceItems(R.array.sort_types, selected,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0)
                                editor.putString("sort_type", "popular");
                            else if (which == 1)
                                editor.putString("sort_type", "top_rated");
                            else if (which == 2)
                                editor.putString("sort_type","favourite");
                        }
                    });
            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    editor.commit();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMovies extends AsyncTask<String, Void, Void> {

        String LOG_TAG = "FetchMovies";

        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            posters = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + params[0] + "?api_key=" + api_key);
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
                moviesJsonStr = buffer.toString();
                Log.d(LOG_TAG, "JSON Parsed: " + moviesJsonStr);

                JSONObject main = new JSONObject(moviesJsonStr);
                JSONArray arr = main.getJSONArray("results");
                JSONObject movie;
                arrayList.clear();
                for(int i =0; i < arr.length(); i++) {
                    movie = arr.getJSONObject(i);
                    ids.add(movie.getInt("id"));
                    posters.add(movie.getString("poster_path"));
                    model = new ModelDataMain(image_url + movie.getString("poster_path"));
                    arrayList.add(model);
                }
                Log.d(LOG_TAG, "Posters:" + posters);
                Log.d(LOG_TAG, "IDs:" + ids);

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
            imageAdapter = new Adapter(getApplicationContext(), arrayList);
            try {
                gridView.setAdapter(imageAdapter);
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "Error", e);
            }
        }
    }



}
