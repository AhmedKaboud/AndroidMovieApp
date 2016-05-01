package com.example.kaboud.moviesapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class MainFragment extends Fragment {

    GridView MovieGrid = null;
    Movie[] resultArr;
    MovieLestiner movieLestiner;

    public MainFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
////////////////////////////////////////
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Type = sharedPref.getString(
                getString(R.string.pref_select_movie_key),
                getString(R.string.pref_Movies_popular));

////////////////////////////////////////
        if (!Type.equals(getString(R.string.pref_Movies_Favorites)))
            new FetchMovieDataTask().execute(Type);
        else {
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(getActivity()).build();
            Realm realm = Realm.getInstance(realmConfig);
            RealmResults<FavoriteMovie> results = realm.where(FavoriteMovie.class).findAll();

            Movie[] movies = new Movie[results.size()];

            for (int i = 0; i < results.size(); i++) {
                FavoriteMovie FM = results.get(i);

                Movie m = new Movie();
                m.setID(FM.getID());
                m.setTitle(FM.getTitle());
                m.setOverview(FM.getOverview());
                m.setPosterURL(FM.getPosterURL());
                m.setRate(FM.getRate());
                m.setReleaseDate(FM.getReleaseDate());

                movies[i] = m;
            }
            resultArr = movies;
            if (movies.length != 0) {
                MovieGridAdapter MovieAdapter = new MovieGridAdapter(getActivity(), movies);
                MovieGrid.setAdapter(MovieAdapter);
            }
        }

    }

    public void SetMovieLstiner(MovieLestiner ml) {
        movieLestiner = ml;
    }

    public interface MovieLestiner {
        public void onItemSelected(Movie movie);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        MovieGrid = (GridView) rootView.findViewById(R.id.gridview);

        MovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = resultArr[position];
                movieLestiner.onItemSelected(m);
            }
        });

        return rootView;
    }

    /*
      class for connecting api of Movies and do it in Background thread not main thread
    */
    public class FetchMovieDataTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String MoviesJsonStr)
                throws JSONException {

            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String TITLE = "title";
            final String RELEASE_DATE = "release_date";
            final String RATE = "vote_average";
            final String ID = "id";

            JSONObject MoviesJson = new JSONObject(MoviesJsonStr);
            JSONArray MoviesArray = MoviesJson.getJSONArray("results");

            resultArr = new Movie[MoviesArray.length()];

            for (int i = 0; i < MoviesArray.length(); ++i) {

                JSONObject SingleMovieJsn = MoviesArray.getJSONObject(i);

                Movie m = new Movie();
                m.setPosterURL(SingleMovieJsn.getString(POSTER_PATH));
                m.setOverview(SingleMovieJsn.getString(OVERVIEW));
                m.setTitle(SingleMovieJsn.getString(TITLE));
                m.setReleaseDate(SingleMovieJsn.getString(RELEASE_DATE));
                m.setRate(SingleMovieJsn.getString(RATE));
                m.setID(SingleMovieJsn.getInt(ID));

                resultArr[i] = m;
            }
            return resultArr;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String MoviesJsonStr = null;

            //Press HERE API_KEY
            String Key = "";
            try {

                final String MOVIESDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String KEY_PARAM = "api_key";

                Uri BuiltUri = Uri.parse(MOVIESDB_BASE_URL).buildUpon()
                        .appendEncodedPath(params[0])
                        .appendQueryParameter(KEY_PARAM, Key)
                        .build();

                URL url = new URL(BuiltUri.toString());
                // Create the request to theMovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }

                MoviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON String = " + MoviesJsonStr);
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
            try {
                return getMovieDataFromJson(MoviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            MovieGridAdapter MovieAdapter = new MovieGridAdapter(getActivity(), movies);
            MovieGrid.setAdapter(MovieAdapter);
        }

    }

}
