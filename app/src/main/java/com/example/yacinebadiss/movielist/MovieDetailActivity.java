package com.example.yacinebadiss.movielist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MovieDetailActivity extends AppCompatActivity {
    public static String MOVIE_NAME = "MOVIE_NAME";
    public static String MOVIE_ID = "MOVIE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            String movieId = getIntent().getStringExtra(MOVIE_ID);
            String movieName = getIntent().getStringExtra(MOVIE_NAME);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MovieDetailFragment.newInstance(movieName, movieId))
                    .commit();
        }
    }
}
