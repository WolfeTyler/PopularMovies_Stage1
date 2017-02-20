package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
* Resources used as guidance:
* AsyncTask - http://stackoverflow.com/questions/9671546/asynctask-android-example
* URL Connection - http://stackoverflow.com/questions/36853964/basic-authentication-android-urlconnection-get
* JSONObjects - https://developer.android.com/reference/org/json/JSONObject.html
* https://www.javacodegeeks.com/2013/10/android-json-tutorial-create-and-parse-json-data.html
* Querying data from TMDB - http://stackoverflow.com/questions/32332807/how-to-request-reviews-of-a-movie-from-tmdb-using-their-json-api
*/


class MovieAsyncTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = MovieAsyncTask.class.getSimpleName();
    private final String mApiKey;
    private final OnTaskCompleted mListener;


    public MovieAsyncTask(OnTaskCompleted listener, String apiKey) {
        super();
        mListener = listener;
        mApiKey = apiKey;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        try {
            URL url = getApiUrl(params);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            if (builder.length() == 0) {
                return null;
            }
            moviesJsonStr = builder.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error I/O Exception ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error I/O Close", e);
                }
            }
        }
        try {
            return getJsonMovieData(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Movie[] getJsonMovieData(String moviesJsonStr) throws JSONException {

        final String OWM_RESULTS = "results";
        final String OWM_ORIGINAL_TITLE = "original_title";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_OVERVIEW = "overview";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_POPULARITY = "popularity";

        JSONObject jsonMovieObject = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = jsonMovieObject.getJSONArray(OWM_RESULTS);
        Movie[] movies = new Movie[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            movies[i] = new Movie();
            JSONObject movieInfo = resultsArray.getJSONObject(i);
            movies[i].setMovieTitle(movieInfo.getString(OWM_ORIGINAL_TITLE));
            movies[i].setPoster(movieInfo.getString(OWM_POSTER_PATH));
            movies[i].setSynopsis(movieInfo.getString(OWM_OVERVIEW));
            movies[i].setVoteAverage(movieInfo.getDouble(OWM_VOTE_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(OWM_RELEASE_DATE));
            movies[i].setPopularity(movieInfo.getInt(OWM_POPULARITY));
        }
        return movies;
    }

    private URL getApiUrl(String[] parameters) throws MalformedURLException {

        final String SORT_BY_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
//        final String tmdbUrl = "https://api.themoviedb.org/3/discover/movie?";
        final String tmdbUrl = "http://api.themoviedb.org/3/movie/popular?";
        Uri builtUri = Uri.parse(tmdbUrl).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, parameters[0])
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();
        return new URL(builtUri.toString());
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        mListener.onTaskCompleted(movies);
    }
}
