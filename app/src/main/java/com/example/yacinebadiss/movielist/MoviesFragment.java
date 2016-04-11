package com.example.yacinebadiss.movielist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONObject;

import java.util.List;

public class MoviesFragment extends Fragment implements MovieAsyncResponse {
    private ImageAdapter mMoviesAdapter;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mMoviesAdapter = new ImageAdapter(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        final GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);
        gridView.setAdapter(mMoviesAdapter);

        getMovieConfig();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshMovieList();
    }

    private void getMovieConfig() {
        new MovieAsyncRetriever(this, MovieAsyncRetriever.DO_CONFIG_REQ).execute();
    }
    private void refreshMovieList() {
        new MovieAsyncRetriever(this, MovieAsyncRetriever.DO_LIST_REQ).execute();
    }

    @Override
    public void processMovieList(List<Movie> output) {
        mMoviesAdapter.addAll(output);
    }

    @Override
    public void processVideos(JSONObject output) {

    }
}
