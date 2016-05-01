package com.example.kaboud.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Kaboud on 3/25/2016.
 */

public class MovieGridAdapter extends BaseAdapter{

    private Context LayoutContext;
    private Movie[] Movies;


    public MovieGridAdapter(Context c,Movie[] M) {
        LayoutContext = c;
        Movies = M;
    }

    @Override
    public int getCount() {
        return Movies.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        //View Currentview;
        ImageView imgView = null;
        TextView txtView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)LayoutContext.getSystemService(LayoutContext.LAYOUT_INFLATER_SERVICE);
            //convertView = new View(LayoutContext);
            convertView = inflater.inflate(R.layout.grid_item, null);

        }
        txtView = (TextView)convertView.findViewById(R.id.textViewItem);
        imgView = (ImageView)convertView.findViewById(R.id.imageViewItem);
        Picasso.with(LayoutContext).load("http://image.tmdb.org/t/p/w342" + Movies[position].getPosterURL() ).into(imgView);
        txtView.setText(Movies[position].getTitle());

        return convertView;
    }

}
