package com.example.yacinebadiss.movielist;

import android.graphics.drawable.Drawable;

/**
 * Created by yacinebadiss on 09/04/2016.
 */
public class Movie {
    public String mId;
    public String mTitle;
    public Drawable mPoster;

    Movie(String id, String title, Drawable poster) {
        mId = id;
        mTitle = title;
        mPoster = poster;
    }
}
