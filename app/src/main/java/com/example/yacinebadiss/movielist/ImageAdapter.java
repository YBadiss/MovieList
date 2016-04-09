package com.example.yacinebadiss.movielist;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by yacinebadiss on 09/04/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Movie[] mItems;

    public ImageAdapter(Context c) {
        mContext = c;
        mItems = new Movie[0];
    }

    public int getCount() {
        return mItems.length;
    }

    public Object getItem(int position) {
        return mItems[position];
    }

    public long getItemId(int position) {
        return Long.parseLong(mItems[position].mId);
    }

    public void addAll(Movie[] items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, final View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            imageView = (ImageView) inflater.inflate(R.layout.grid_item_movie, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }
        Movie m = mItems[position];
        imageView.setImageDrawable(m.mPoster);
        imageView.setTag(R.id.tag_movie_id, m.mId);
        imageView.setTag(R.id.tag_movie_title, m.mTitle);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, (String) v.getTag(R.id.tag_movie_title), Toast.LENGTH_SHORT).show();
            }
        });
        return imageView;
    }
}
