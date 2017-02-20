package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.ParseException;

/*
* Resources used as guidance:
* Parcelable Tutorial Used - https://www.sitepoint.com/transfer-data-between-activities-with-android-parcelable/
* Working w/ Picasso - http://stacktips.com/tutorials/android/how-to-use-picasso-library-in-android
* Using a LOG_TAG - https://coderwall.com/p/bq7nya/proper-log_tag-on-android
*/

public class MovieDetails extends AppCompatActivity {

    private final String LOG_TAG = MovieDetails.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        ImageView ivMoviePoster = (ImageView) findViewById(R.id.imageview_poster);
        TextView tvVoteAverage = (TextView) findViewById(R.id.textview_vote_average);
        TextView tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);
        TextView tvMovieTitle = (TextView) findViewById(R.id.textview_title);
        TextView tvSynopsis = (TextView) findViewById(R.id.textview_synopsis);
        TextView tvPopularity = (TextView) findViewById(R.id.textview_popularity);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_movie));
        String releaseDate = movie.getReleaseDate();
        String synopsis = movie.getSynopsis();
        Picasso.with(this)
                .load(movie.getPoster())
                .resize(getResources().getInteger(R.integer.tmdb_poster_width),
                        getResources().getInteger(R.integer.tmdb_poster_height))
                .error(R.drawable.not_found)
                .placeholder(R.drawable.loading)
                .into(ivMoviePoster);


        if(releaseDate != null) {
            try {
                releaseDate = ReleaseDateFormatter.getLocalizedDate(this,
                        releaseDate, movie.getDateFormat());
            } catch (ParseException e) { Log.e(LOG_TAG, "Error getting Release Date", e); }
        } else {
            releaseDate = getResources().getString(R.string.no_release_date_found);
        }

        if (synopsis == null) { synopsis = getResources().getString(R.string.no_synopsis_found); }

        tvReleaseDate.setText(releaseDate);
        tvVoteAverage.setText(movie.getDisplayVoteAverage());
        tvMovieTitle.setText(movie.getMovieTitle());
        tvSynopsis.setText(synopsis);
        tvPopularity.setText(movie.getDisplayPopularity());

    }
}