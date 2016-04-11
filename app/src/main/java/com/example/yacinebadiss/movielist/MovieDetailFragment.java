package com.example.yacinebadiss.movielist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MovieDetailFragment extends Fragment implements YouTubePlayer.OnInitializedListener, MovieAsyncResponse{
    private static String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static String YOUTUBE_VIDEO_ID = "ccZqnCTPgEc";
    private static String YOUTUBE_KEY = "AIzaSyDN7HQ9JO1OIEkKnAuvV7S9s1L94TEVieg";

    private String mMovieName;
    private String mMovieId;
    private String mVideoId;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment newInstance(String movieName, String movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putString(MovieDetailActivity.MOVIE_NAME, movieName);
        args.putString(MovieDetailActivity.MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieName = getArguments().getString(MovieDetailActivity.MOVIE_NAME);
            mMovieId = getArguments().getString(MovieDetailActivity.MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.fragment_movie_detail_textview);
        textView.setText(mMovieName);

        getMovieVideos();
        return rootView;
    }

    @Override
    public void onInitializationSuccess(
            YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(mVideoId);
        }
    }

    private void getMovieVideos() {
        new MovieAsyncRetriever(this, MovieAsyncRetriever.DO_VIDEOS_REQUEST).execute(mMovieId);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.e(LOG_TAG, "FAILED TO INITIALIZE YOUTUBE FRAGMENT");
    }

    @Override
    public void processMovieList(List<Movie> output) {

    }

    @Override
    public void processVideos(JSONObject output) {
        mVideoId = YOUTUBE_VIDEO_ID;
        Log.w(LOG_TAG, output.toString());
        try {
            JSONArray results = output.getJSONArray("results");
            for (int i = 0; i < results.length(); ++i) {
                JSONObject obj = results.getJSONObject(i);
                Log.w(LOG_TAG, obj.toString());
                if (obj.getString("type").equals("Trailer") && obj.getString("site").equals("YouTube")) {
                    mVideoId = obj.getString("key");
                    break;
                }
                else {
                    Log.w(LOG_TAG, "Nope");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Activity activity = getActivity();
        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) activity.getFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(YOUTUBE_KEY, this);
    }
}
