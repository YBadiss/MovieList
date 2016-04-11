package com.example.yacinebadiss.movielist;

import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yacinebadiss on 09/04/2016.
 */
public class Movie {
    public static String BASE_IMG_PATH;

    public String mId;
    public String mTitle;
    public Drawable mPoster;

    Movie(JSONObject jsonObject) {
        try {
            mId = jsonObject.getString("id");
            mTitle = jsonObject.getString("original_title");
            String posterPath = makePosterPath(jsonObject.getString("poster_path"));
            InputStream is = (InputStream) new URL(posterPath).getContent();
            mPoster = Drawable.createFromStream(is, "src name");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String makePosterPath(String posterImgName) {
        if (posterImgName == "null") {
            posterImgName = "/6bCplVkhowCjTHXWv49UjRPn0eK.jpg";
        }
        return BASE_IMG_PATH + posterImgName;
    }
}
