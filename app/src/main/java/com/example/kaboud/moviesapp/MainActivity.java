package com.example.kaboud.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainFragment.MovieLestiner {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // if (savedInstanceState == null) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.SetMovieLstiner(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_MainActivity, mainFragment)
                .commit();
        //  }
        if (findViewById(R.id.flayout2) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {

        if (mTwoPane) {
            MovieDetailFragment fragment = new MovieDetailFragment();
            Bundle args = new Bundle();
            args.putSerializable("Movie", movie);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.flayout2, fragment).commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra("Movie", movie);
            startActivity(intent);
        }
    }
}
