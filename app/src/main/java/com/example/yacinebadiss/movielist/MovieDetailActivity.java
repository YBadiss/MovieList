package com.example.yacinebadiss.movielist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            String movieName = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MovieDetailFragment.newInstance(movieName))
                    .commit();
        }
    }
}
