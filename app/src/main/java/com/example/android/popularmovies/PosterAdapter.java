package com.example.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

/*
* Resources used as guidance:
* Using BaseAdapter - http://abhiandroid.com/ui/baseadapter-tutorial-example.html
* Adapter Overview - https://www.youtube.com/watch?v=uic3TVp_j3M
* Working w/ Picasso - http://stacktips.com/tutorials/android/how-to-use-picasso-library-in-android
* Tutorial - https://www.learn2crack.com/2014/01/android-custom-gridview.html
*/

class PosterAdapter extends BaseAdapter {
    private final Context mContext;
    private final Movie[] mMovies;

    public PosterAdapter(Context context, Movie[] movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public int getCount() {
        if (mMovies == null || mMovies.length == 0) {
            return -1;
        }
        return mMovies.length;
    }

    @Override
    public Movie getItem(int position) {
        if (mMovies == null || mMovies.length == 0) {
            return null;
        }
        return mMovies[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mMovies[position].getPoster())
                .resize(mContext.getResources().getInteger(R.integer.tmdb_poster_width),
                        mContext.getResources().getInteger(R.integer.tmdb_poster_height))
                .error(R.drawable.not_found)
                .placeholder(R.drawable.loading)
                .into(imageView);
        return imageView;
    }
}
