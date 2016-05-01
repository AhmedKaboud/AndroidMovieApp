package com.example.kaboud.moviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MovieDetailFragment extends Fragment implements View.OnClickListener{

    RealmAsyncTask transaction;
    RealmConfiguration realmConfig;
    RealmResults<FavoriteMovie> FavMov;

    // Get a Realm instance for this thread
    Realm realm;
    Movie movie;
    LinearLayout list_Reviews = null;
    LinearLayout list_Trailers = null;
    ImageButton imgBtn;

    FetchMovieTrailerReviewsTask f1 = new FetchMovieTrailerReviewsTask();
    FetchMovieTrailerReviewsTask f2 = new FetchMovieTrailerReviewsTask();

    ///

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realmConfig = new RealmConfiguration.Builder(getActivity()).build();
        realm = Realm.getInstance(realmConfig);

        Bundle x = getArguments();
        if (x != null) {
            movie = (Movie) x.getSerializable("Movie");
        }

        f1.execute(0);      //0 to get Trailers then set movie object
        f2.execute(1);      //1 to get Reviews then set movie object

        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ImageView imgView = (ImageView) rootView.findViewById(R.id.movie_poster);
        TextView title = (TextView) rootView.findViewById(R.id.movie_title);
        TextView overview = (TextView) rootView.findViewById(R.id.movie_overview);
        TextView date = (TextView) rootView.findViewById(R.id.movie_date);
        TextView rate = (TextView) rootView.findViewById(R.id.movie_rate);

        imgBtn = (ImageButton) rootView.findViewById(R.id.favorite);
        imgBtn.setOnClickListener(listen);

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w154" + movie.getPosterURL()).into(imgView);
        title.setText(movie.getTitle().toString());
        overview.setText(movie.getOverview().toString());
        date.setText(movie.getReleaseDate().toString());
        rate.setText(movie.getRate().toString() + "/10");


        list_Reviews = (LinearLayout) rootView.findViewById(R.id.review_list);
        list_Trailers = (LinearLayout) rootView.findViewById(R.id.trialer_list);

        //find if this film is favorite or not
        if(FavMov!= null)
            FavMov.clear();
        FavMov = realm.where(FavoriteMovie.class).equalTo("ID", movie.getID()).findAll();

        if (FavMov.size() == 0) {
            imgBtn.setTag(2);
            imgBtn.setImageResource(android.R.drawable.btn_star_big_off);
        } else {
            imgBtn.setTag(1);
            imgBtn.setImageResource(android.R.drawable.btn_star_big_on);
        }

        return rootView;
    }

    View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.txt_Trailer){
                int position = (int)v.getTag();
                MovieTrailer[] movieTrailArr =  movie.getMovieTrailerArr();
                MovieTrailer mr = movieTrailArr[position];
                String url = "https://www." + mr.getSite() + ".com/watch?v=" + mr.getKey();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else if(v.getId() == R.id.txt_Review){
                int position = (int)v.getTag();
                if(position < 0) {
                    MovieReview mr = movie.getMovieReviewArr()[(0-position)-1];
                    ((TextView) v).setText(mr.getAuther() + "\n\t" + mr.getContent());
                    v.setTag(0 - position);
                }
                else {
                    MovieReview mr = movie.getMovieReviewArr()[position-1];
                    v.setTag(0-position);
                    if (mr.getContent().length() >= 20)
                        ((TextView) v).setText(mr.getAuther() + "\n\t" + mr.getContent().substring(0, 20) + "....");
                }
            }else if(v.getId() == R.id.favorite){
                if (imgBtn.getTag() == 1) {

                    realm.beginTransaction();
                    FavMov.clear();     // Delete all matches
                    realm.commitTransaction();
                    Toast.makeText(getActivity(), "Sucessfully Removed from Favorite.", Toast.LENGTH_SHORT).show();

                    imgBtn.setImageResource(android.R.drawable.btn_star_big_off);
                    imgBtn.setTag(2);
                    /////////////////////////////////////
                } else {
                    imgBtn.setImageResource(android.R.drawable.btn_star_big_on);
                    imgBtn.setTag(1);
                    //////////////////////////////////////
                    transaction = realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            FavoriteMovie FMovie = bgRealm.createObject(FavoriteMovie.class);
                            FMovie.setTitle(movie.getTitle());
                            FMovie.setOverview(movie.getOverview());
                            FMovie.setReleaseDate(movie.getReleaseDate());
                            FMovie.setPosterURL(movie.getPosterURL());
                            FMovie.setRate(movie.getRate());
                            FMovie.setID(movie.getID());
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getActivity(), "Sucessfully Added to Favorite.", Toast.LENGTH_SHORT).show();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(getActivity(), "Failed Added to Favorite.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }


        }
    };

    private String[] getReviewsList() {
        MovieReview[] reviews = movie.getMovieReviewArr();
        int length = reviews.length;
        String[] reviewsStrings = new String[length];
        int i = 0;
        for (MovieReview review : reviews) {
            if (review.getContent().length() >= 20)
                reviewsStrings[i++] = review.getAuther() + "\n\t" + review.getContent().substring(0, 20) + "....";
            else
                reviewsStrings[i++] = review.getAuther() + "\n\t" + review.getContent() + ".";
        }
        return reviewsStrings;
    }

    private String[] getTrailersList() {
        MovieTrailer[] Trailers = movie.getMovieTrailerArr();
        int length = Trailers.length;
        String[] TrailerStrings = new String[length];
        int i = 0;
        for (MovieTrailer movieTrailer : Trailers) {
            TrailerStrings[i++] = movieTrailer.getName();
        }
        return TrailerStrings;
    }

    @Override
    public void onStop() {
        if (transaction != null && !transaction.isCancelled()) {
            transaction.cancel();
        }
        f1.cancel(true);
        f2.cancel(true);
        super.onStop();
    }

    @Override
    public void onClick(View v) {

    }

    public class FetchMovieTrailerReviewsTask extends AsyncTask<Integer, Void, Wrapper> {

        private final String LOG_TAG = FetchMovieTrailerReviewsTask.class.getSimpleName();

        private MovieTrailer[] getTrailerFromJson(String TrailersJsonStr)
                throws JSONException {

            final String ID = "id";
            final String KEY = "key";
            final String NAME = "name";
            final String SITE = "site";

            JSONObject TrailerJson = new JSONObject(TrailersJsonStr);
            JSONArray TrailerArray = TrailerJson.getJSONArray("results");

            MovieTrailer[] resultArr = new MovieTrailer[TrailerArray.length()];

            for (int i = 0; i < TrailerArray.length(); ++i) {

                JSONObject SingleMovieJsn = TrailerArray.getJSONObject(i);

                MovieTrailer mt = new MovieTrailer();
                mt.setId(SingleMovieJsn.getString(ID));
                mt.setKey(SingleMovieJsn.getString(KEY));
                mt.setName(SingleMovieJsn.getString(NAME));
                mt.setSite(SingleMovieJsn.getString(SITE));

                resultArr[i] = mt;
            }
            return resultArr;
        }

        private MovieReview[] getReviewFromJson(String ReviewsJsonStr)
                throws JSONException {

            final String ID = "id";
            final String AUTHER = "author";
            final String CONTENT = "content";
            final String URL = "url";

            JSONObject ReviewJson = new JSONObject(ReviewsJsonStr);
            JSONArray ReviewArray = ReviewJson.getJSONArray("results");

            MovieReview[] resultArr = new MovieReview[ReviewArray.length()];

            for (int i = 0; i < ReviewArray.length(); ++i) {

                JSONObject SingleMovieJsn = ReviewArray.getJSONObject(i);

                MovieReview mr = new MovieReview();
                mr.setId(SingleMovieJsn.getString(ID));
                mr.setAuther(SingleMovieJsn.getString(AUTHER));
                mr.setContent(SingleMovieJsn.getString(CONTENT));
                mr.setURL(SingleMovieJsn.getString(URL));

                resultArr[i] = mr;
            }
            return resultArr;
        }

        @Override
        protected Wrapper doInBackground(Integer... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String MoviesJsonStr = null;

            //Press HERE API_KEY
            String Key = "";

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String KEY_PARAM = "api_key";
                final String ID_PARAM = Integer.toString(movie.getID());
                final String VIDEOS_PARAM = "videos";
                final String REVIEWS_PARAM = "reviews";

                Uri BuiltUri = null;
                if (params[0] == 0) {
                    BuiltUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(ID_PARAM)
                            .appendEncodedPath(VIDEOS_PARAM)
                            .appendQueryParameter(KEY_PARAM, Key)
                            .build();
                } else if (params[0] == 1) {
                    BuiltUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(ID_PARAM)
                            .appendEncodedPath(REVIEWS_PARAM)
                            .appendQueryParameter(KEY_PARAM, Key)
                            .build();
                }

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
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                if (params[0] == 0)
                    return new Wrapper(0, getTrailerFromJson(MoviesJsonStr));
                else
                    return new Wrapper(1, getReviewFromJson(MoviesJsonStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Wrapper wrap) {

            if (wrap.flag == 0) {
                MovieTrailer[] trailers = new MovieTrailer[wrap.objects.length];
                MovieTrailer mt = new MovieTrailer();
                int i = 0;
                for (Object obj : wrap.objects) {
                    mt = MovieTrailer.class.cast(obj);
                    trailers[i++] = mt;
                }
                movie.setMovieTrailerArr(trailers);

                String[] TrailersList = getTrailersList();

                for (int j = 0; j < TrailersList.length; j++) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    View TrailerView = inflater.inflate(R.layout.trailer_movie_item, null);
                    TextView txtV = (TextView) TrailerView.findViewById(R.id.txt_Trailer);
                    txtV.setText(TrailersList[j]);
                    txtV.setTag(j);
                    txtV.setOnClickListener(listen);
                    list_Trailers.addView(txtV);
                }
            } else {
                MovieReview[] reviews = new MovieReview[wrap.objects.length];
                MovieReview mr = new MovieReview();
                int i = 0;
                for (Object obj : wrap.objects) {
                    mr = MovieReview.class.cast(obj);
                    reviews[i++] = mr;
                }
                movie.setMovieReviewArr(reviews);

                String[] ReviewsList = getReviewsList();
                for (int j = 1; j <= ReviewsList.length; j++) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    View ReviewView = inflater.inflate(R.layout.review_movie_item, null);
                    TextView txtV = (TextView) ReviewView.findViewById(R.id.txt_Review);
                    txtV.setText(ReviewsList[j - 1]);
                    txtV.setTag(0 - j);
                    txtV.setOnClickListener(listen);
                    list_Reviews.addView(txtV);
                }
            }
        }

    }

    public class Wrapper{
        int flag;
        Object[] objects;

        public Wrapper(int f,Object[] ob){
            flag = f;
            objects = ob;
        }
    }
}
