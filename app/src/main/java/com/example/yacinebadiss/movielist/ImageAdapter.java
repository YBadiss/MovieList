package com.example.yacinebadiss.movielist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yacinebadiss on 09/04/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<Movie> mItems;

    public ImageAdapter(Activity activity) {
        mActivity = activity;
        mItems = new ArrayList<>();
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return Long.parseLong(mItems.get(position).mId);
    }

    public void addAll(List<Movie> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, final View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            imageView = (ImageView) inflater.inflate(R.layout.grid_item_movie, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }
        Movie m = mItems.get(position);
        imageView.setImageDrawable(m.mPoster);
        imageView.setTag(R.id.tag_movie_id, m.mId);
        imageView.setTag(R.id.tag_movie_title, m.mTitle);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE_ID, (String) v.getTag(R.id.tag_movie_id));
                intent.putExtra(MovieDetailActivity.MOVIE_NAME, (String) v.getTag(R.id.tag_movie_title));
                mActivity.startActivity(intent);
            }
        });
        return imageView;
    }
}
