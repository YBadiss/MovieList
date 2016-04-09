package com.example.yacinebadiss.movielist;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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

public class MoviesFragment extends Fragment {
    public static String DO_CONFIG_REQ = "CONFIG";
    public static String DO_DATA_REQ = "DATA";

    private ImageAdapter mMoviesAdapter;
    private String mBaseImgPath;

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
        new MoviesAsyncRetrieve(DO_CONFIG_REQ).execute();
    }
    private void refreshMovieList() {
        new MoviesAsyncRetrieve(DO_DATA_REQ).execute();
    }

    private class MoviesAsyncRetrieve extends AsyncTask<Void, Void, Object> {
        private String BASE_URL = "http://api.themoviedb.org/3/";
        private String API_KEY = "9393b3e6bb28cf602a3a6c0392499122";
        private String LOG_TAG = MoviesAsyncRetrieve.class.getSimpleName();
        private Uri CONFIG_URI = Uri.parse(BASE_URL).buildUpon().appendPath("configuration")
                                    .appendQueryParameter("api_key", API_KEY)
                                    .build();
        private Uri MOVIES_URI = Uri.parse(BASE_URL).buildUpon()
                                    .appendPath("discover").appendPath("movie")
                                    .appendQueryParameter("api_key", API_KEY)
                                    .appendQueryParameter("primary_release_date.gte", "2016-04-01")
                                    .appendQueryParameter("primary_release_date.lte", "2016-05-01")
                                    .appendQueryParameter("sort_by", "popularity.desc")
                                    .appendQueryParameter("language", "en")
                                    .build();
        private String mAction;

        MoviesAsyncRetrieve(String action) {
            super();
            mAction = action;
        }

        @Override
        protected Object doInBackground(Void... params) {
            if (mAction == DO_CONFIG_REQ) {
                return doRequest(CONFIG_URI);
            } else if (mAction == DO_DATA_REQ) {
                JSONObject jsonObject = doRequest(MOVIES_URI);
                return getMovies(jsonObject);
            } else {
                return null;
            }
        }

        private String makePosterPath(String posterImgName) {
            if (posterImgName == "null") {
                posterImgName = "/6bCplVkhowCjTHXWv49UjRPn0eK.jpg";
            }
            return mBaseImgPath + posterImgName;
        }

        private Movie[] getMovies(JSONObject result) {
            List<Movie> images = new ArrayList<>();
            try {
                JSONArray moviesArray = result.getJSONArray("results");
                for (int i = 0; i < moviesArray.length(); ++i) {
                    JSONObject obj = (JSONObject) moviesArray.get(i);
                    String id = obj.getString("id");
                    String title = obj.getString("original_title");
                    String posterPath = makePosterPath(obj.getString("poster_path"));
                    InputStream is = (InputStream) new URL(posterPath).getContent();
                    images.add(new Movie(
                            id,
                            title,
                            Drawable.createFromStream(is, "src name")));
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return images.toArray(new Movie[images.size()]);
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
                        mBaseImgPath = Uri.parse(((JSONObject) result).getJSONObject("images").getString("base_url"))
                                        .buildUpon().appendPath("w342").toString();
                    } else if (mAction == DO_DATA_REQ) {
                        mMoviesAdapter.addAll((Movie[]) result);
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
}
