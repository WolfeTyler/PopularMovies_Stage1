package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/*
* Resources used as guidance:
* Parcelable Tutorial Used - https://www.sitepoint.com/transfer-data-between-activities-with-android-parcelable/
* Working w/ Parcelable - http://stackoverflow.com/questions/4778834/purpose-of-describecontents-of-parcelable-interface
* http://stackoverflow.com/questions/19424500/parcelable-objects
* TMDB Tutorial - https://richardroseblog.wordpress.com/2016/07/01/the-movie-database-tmdb-api/
*
*/

//Establishes each Movie element variable w/ set & get functions
//Setting up the Parcelable for use b/t MainActivity & MovieDetails
public class Movie implements Parcelable {

    private String mMovieTitle;
    private String mPoster;
    private String tmdbUrl = "https://image.tmdb.org/t/p/w185";
    private String mSynopsis;
    private String mReleaseDate;
    private Double mVoteAverage;
    private Integer mPopularity;
    private String dateFormat = "MM-dd-yyyy";

    public Movie() {}

    public void setMovieTitle(String movieTitle) { mMovieTitle = movieTitle; }
    public String getMovieTitle() { return mMovieTitle; }

    public void setPoster(String poster) { mPoster = poster; }
    public String getPoster() { return tmdbUrl + mPoster; }

    public void setSynopsis(String movieSynopsis) {
        if(!movieSynopsis.equals("null")) {
            mSynopsis = movieSynopsis;
        }
    }
    public String getSynopsis() { return mSynopsis; }

    public void setReleaseDate(String releaseDate) {
        if(!releaseDate.equals("null")) {
            mReleaseDate = releaseDate;
        }
    }
    public String getReleaseDate() { return mReleaseDate; }
    public String getDateFormat() { return dateFormat; }

    public void setVoteAverage(Double voteAverage) { mVoteAverage = voteAverage; }
    private Double getVoteAverage() { return mVoteAverage; }
    public String getDisplayVoteAverage() { return String.valueOf(getVoteAverage())+"/10.0"; }

    public void setPopularity(Integer popularity) { mPopularity = popularity; }
    private Integer getPopularity() { return mPopularity; }
    public String getDisplayPopularity() { return String.valueOf(getPopularity()); }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieTitle);
        dest.writeString(mPoster);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeValue(mVoteAverage);
        dest.writeValue(mPopularity);
    }

    public Movie(Parcel in) {
        mMovieTitle = in.readString();
        mPoster = in.readString();
        mSynopsis = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = (Double) in.readValue(Double.class.getClassLoader());
        mPopularity = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}