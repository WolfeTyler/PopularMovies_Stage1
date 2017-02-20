package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/*
* Resources used as guidance:
* GridView & Adapter Usage - http://www.javatpoint.com/android-gridview-example
* Working w/ Parcelable - http://stackoverflow.com/questions/4778834/purpose-of-describecontents-of-parcelable-interface
* Using onClickListener w/ GridView & Adapter - http://stackoverflow.com/questions/20052631/android-gridview-with-custom-baseadapter-create-onclicklistener
* Adding Menu Items - http://stackoverflow.com/questions/6680570/android-oncreateoptionsmenu-item-action
* Menu Visibility - http://stackoverflow.com/questions/9030268/set-visibility-in-menu-programatically-android
* Working w/ Shared Prefs - http://stackoverflow.com/questions/5946135/difference-between-getdefaultsharedpreferences-and-getsharedpreferences
* Saving & Restoring Instance States - http://stackoverflow.com/questions/4096169/onsaveinstancestate-and-onrestoreinstancestate
*/

public class MainActivity extends AppCompatActivity {

    private GridView mGrid;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGrid = (GridView) findViewById(R.id.gridview);
        mGrid.setOnItemClickListener(posterClickListener);
        if (savedInstanceState == null) {
            getTmdbMovieData(getSortPref());
        } else {
            Parcelable[] parcelable = savedInstanceState.
                    getParcelableArray(getString(R.string.parcel_movie));
            if (parcelable != null) {
                int movieCount = parcelable.length;
                Movie[] movies = new Movie[movieCount];
                for (int p = 0; p < movieCount; p++) {
                    movies[p] = (Movie) parcelable[p];
                }
                mGrid.setAdapter(new PosterAdapter(this, movies));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int movieCount = mGrid.getCount();
        if (movieCount > 0) {
            Movie[] movies = new Movie[movieCount];
            for (int i = 0; i < movieCount; i++) {
                movies[i] = (Movie) mGrid.getItemAtPosition(i);
            }
            outState.putParcelableArray(getString(R.string.parcel_movie), movies);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.string.sort_rank:
                updateSharedPreferences(getString(R.string.tmdb_sort_pop_desc));
                updateMenu();
                getTmdbMovieData(getSortPref());
                return true;
            case R.string.sort_vote_avg:
                updateSharedPreferences(getString(R.string.tmdb_sort_vote_avg_desc));
                updateMenu();
                getTmdbMovieData(getSortPref());
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private final GridView.OnItemClickListener posterClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Movie movie = (Movie) parent.getItemAtPosition(position);
            Intent intent = new Intent(getApplicationContext(), MovieDetails.class);
            intent.putExtra(getResources().getString(R.string.parcel_movie), movie);
            startActivity(intent);
        }
    };

//    private void getTmdbMovieData(String sortMethod) {
//        if (checkNetworkAvailability()) {
//            String tmdbApi = getString(R.string.tmdb_api);
//            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
//                @Override
//                public void onTaskCompleted(Movie[] movies) {
//                    mGrid.setAdapter(new PosterAdapter(getApplicationContext(), movies));
//                }
//            };
//            MovieAsyncTask asyncTask = new MovieAsyncTask(taskCompleted, tmdbApi);
//            asyncTask.execute(sortMethod);
//        } else {
//            Toast.makeText(this, getString(R.string.internet_error_message), Toast.LENGTH_LONG).show();
//        }
//    }

    private void getTmdbMovieData(String sortMethod) {
        String sortPref = getSortPref();
        if (sortPref.equals(getString(R.string.tmdb_sort_pop_desc))) {
            if (checkNetworkAvailability()) {
                String tmdbApi = getString(R.string.tmdb_api);
                OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Movie[] movies) {
                        mGrid.setAdapter(new PosterAdapter(getApplicationContext(), movies));
                    }
                };
                MovieAsyncTask asyncTask = new MovieAsyncTask(taskCompleted, tmdbApi);
                asyncTask.execute(sortMethod);
            } else {
                Toast.makeText(this, getString(R.string.internet_error_message), Toast.LENGTH_LONG).show();
            }
        } else {
            if (checkNetworkAvailability()) {
                String tmdbApi = getString(R.string.tmdb_api);
                OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Movie[] movies) {
                        mGrid.setAdapter(new PosterAdapter(getApplicationContext(), movies));
                    }
                };
                MovieAsyncTask_TopRated asyncTask = new MovieAsyncTask_TopRated(taskCompleted, tmdbApi);
                asyncTask.execute(sortMethod);
            } else {
                Toast.makeText(this, getString(R.string.internet_error_message), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkNetworkAvailability() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, mMenu);
        mMenu = menu;
        mMenu.add(Menu.NONE, R.string.sort_rank, Menu.NONE, null)
                .setVisible(false)
                .setIcon(R.drawable.popular_topic)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mMenu.add(Menu.NONE, R.string.sort_vote_avg, Menu.NONE, null)
                .setVisible(false)
                .setIcon(R.drawable.numerical_sorting)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        updateMenu();
        return true;
    }

    public void updateMenu() {
        String sortPref = getSortPref();
        if (sortPref.equals(getString(R.string.tmdb_sort_pop_desc))) {
            mMenu.findItem(R.string.sort_rank).setVisible(false);
            mMenu.findItem(R.string.sort_vote_avg).setVisible(true);
        } else {
            mMenu.findItem(R.string.sort_vote_avg).setVisible(false);
            mMenu.findItem(R.string.sort_rank).setVisible(true);
        }
    }


    private String getSortPref() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(R.string.pref_sort_method_key), getString(R.string.tmdb_sort_pop_desc));
    }

    private void updateSharedPreferences(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }
}