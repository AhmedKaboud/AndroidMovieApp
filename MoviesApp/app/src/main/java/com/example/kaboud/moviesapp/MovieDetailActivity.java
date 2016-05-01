package com.example.kaboud.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MovieDetailActivity extends AppCompatActivity {

    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        //if(savedInstanceState == null)
            /**/
            MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra("Movie");
        args.putSerializable("Movie", movie);
        fragment.setArguments(args);

        //getSupportFragmentManager().beginTransaction().replace(R.id.flayout2,fragment).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.DetailActivity, fragment)
                .commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sharemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            if(movie.getMovieTrailerArr().length !=  0){
                MovieTrailer mr = movie.getMovieTrailerArr()[0];
                String url = "https://www." + mr.getSite() + ".com/watch?v=" + mr.getKey();
                String shareBody = url;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "First Trailer");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }



}
