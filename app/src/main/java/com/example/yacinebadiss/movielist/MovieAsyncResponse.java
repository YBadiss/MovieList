package com.example.yacinebadiss.movielist;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by yacinebadiss on 11/04/2016.
 */
public interface MovieAsyncResponse {
    void processMovieList(List<Movie> output);
    void processVideos(JSONObject output);
}