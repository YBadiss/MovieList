package com.example.yacinebadiss.movielist;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yacinebadiss on 11/04/2016.
 */

public class MovieAsyncRetriever extends AsyncTask<Object, Void, Object> {
    private static String BASE_URL = "http://api.themoviedb.org/3/";
    private static String API_KEY = "9393b3e6bb28cf602a3a6c0392499122";
    private static String LOG_TAG = MovieAsyncRetriever.class.getSimpleName();
    private static Uri CONFIG_URI = Uri.parse(BASE_URL).buildUpon().appendPath("configuration")
            .appendQueryParameter("api_key", API_KEY)
            .build();
    private static Uri MOVIES_URI = Uri.parse(BASE_URL).buildUpon()
            .appendPath("discover").appendPath("movie")
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("primary_release_date.gte", "2016-04-01")
            .appendQueryParameter("primary_release_date.lte", "2016-05-01")
            .appendQueryParameter("sort_by", "popularity.desc")
            .appendQueryParameter("language", "en")
            .build();
    public static Integer DO_CONFIG_REQ = 0;
    public static Integer DO_LIST_REQ = 1;
    public static Integer DO_VIDEOS_REQUEST = 2;

    private Integer mAction;
    private MovieAsyncResponse mDelegate;

    MovieAsyncRetriever(MovieAsyncResponse delegate, Integer action) {
        super();
        mDelegate = delegate;
        mAction = action;
    }

    @Override
    protected Object doInBackground(Object... params) {
        if (mAction == DO_CONFIG_REQ) {
            return doRequest(CONFIG_URI);
        } else if (mAction == DO_LIST_REQ) {
            JSONObject jsonObject = doRequest(MOVIES_URI);
            return getMovies(jsonObject);
        } else if (mAction == DO_VIDEOS_REQUEST) {
            String movieId = (String) params[0];
            Uri videosUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath("movie")
                    .appendPath(movieId)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
            return doRequest(videosUri);
        } else {
            return null;
        }
    }

    private List<Movie> getMovies(JSONObject result) {
        List<Movie> images = new ArrayList<>();
        try {
            JSONArray moviesArray = result.getJSONArray("results");
            for (int i = 0; i < moviesArray.length(); ++i) {
                images.add(new Movie((JSONObject) moviesArray.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return images;
    }

    private JSONObject doRequest(Uri uri) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonResponse;

        try {
            Log.d(LOG_TAG, "Sending request " + uri.toString());
            URL url = new URL(uri.toString());// uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonResponse = buffer.toString();
            Log.i(LOG_TAG, jsonResponse);
            return new JSONObject(jsonResponse);
        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        try {
            if (result != null) {
                if (mAction == DO_CONFIG_REQ) {
                    Movie.BASE_IMG_PATH = Uri.parse(((JSONObject) result).getJSONObject("images").getString("base_url"))
                            .buildUpon().appendPath("w342").toString();
                } else if (mAction == DO_LIST_REQ) {
                    mDelegate.processMovieList((List<Movie>) result);
                } else if (mAction == DO_VIDEOS_REQUEST) {
                    mDelegate.processVideos((JSONObject) result);
                }
            }
            else {
                Log.e(LOG_TAG, "onPostExecute with null value");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "FAILED: ", e);
        }
    }
}