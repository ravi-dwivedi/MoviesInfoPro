package moviespart1.project.udacity.android.movieinfopro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListMainActivityFragment extends Fragment {

    private final String TAG = MovieListMainActivityFragment.class.getSimpleName();
    private Context context;
    private MoviesAdapter moviesAdapter;
    private RecyclerView movieListRecyclerView;
    private ArrayList<Movie> Movies;
    private Toolbar mToolbar;
    private GridLayoutManager gridLayout;
    public MovieListMainActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        Movies = MovieList.get(context).getMovies();
        if(Movies.isEmpty()) {
            // Get the Movie Data
            new FetchMoviesTask().execute();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_list_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            MovieList.get(context).clearAll();
            new FetchMoviesTask().execute();
            return true;
        }
        if (id == R.id.action_Showfav) {
            movieListRecyclerView.setAdapter(new MoviesAdapter(getActivity(),MovieList.get(context).getFavouriteMovies()));
        }
        if(id == R.id.action_Hidefav)
        {
            movieListRecyclerView.setAdapter(new MoviesAdapter(getActivity(),Movies));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
            String sort = preference.getString(ApplicationConstants.SORT_KEY, getResources().getString(R.string.str_sortPopularIndex));
            if(MovieList.getSort_order()!=Integer.parseInt(sort))
            {
                MovieList.get(context).clearAll();
                new FetchMoviesTask().execute();
                switch (MovieList.getSort_order())
                {
                    case 0:
                        MovieList.setSort_order(1);
                        break;
                    case 1:
                        MovieList.setSort_order(0);
                        break;
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to Get Preference: ", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_list_main, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

         movieListRecyclerView = (RecyclerView) view.findViewById(R.id.movieRecyclerView);
        int numCols = 2;
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numCols = 3;
        }
        gridLayout = new GridLayoutManager(getActivity(), numCols);
        movieListRecyclerView.setLayoutManager(gridLayout);
        movieListRecyclerView.setAdapter(new MoviesAdapter(getActivity(),Movies));
        //movieListRecyclerView.addOnScrollListener(new
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {   gridLayout.setSpanCount(3);
        }
        else
        {   gridLayout.setSpanCount(2);
        }
    }
    public class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<Movie>>
    {
        private final String TAG = FetchMoviesTask.class.getSimpleName();
        private ArrayList<Movie> parseMovieData(String jsonStr) throws JSONException {
            ArrayList<Movie> movies = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Movie movie;
            for(int i=0; i < jsonArray.length(); ++i) {
                JSONObject movieJson = jsonArray.getJSONObject(i);
                movie = getMovieFromJson(movieJson);
                movies.add(movie);
            }

            return movies;
        }

        private  Movie getMovieFromJson(JSONObject movieJson) throws JSONException {
            Movie movie = new Movie();
            movie.setTitle(movieJson.getString("title"));
            movie.setPosterPath(movieJson.getString("poster_path"));
            movie.setAdult(movieJson.getBoolean("adult"));
            movie.setOverview(movieJson.getString("overview"));
            movie.setReleaseDate(movieJson.getString("release_date"));
            movie.setBackdoorPath(movieJson.getString("backdrop_path"));
            movie.setPopularity(movieJson.getDouble("popularity"));
            movie.setVoteCount(movieJson.getInt("vote_count"));
            movie.setRating(movieJson.getDouble("vote_average"));

            JSONArray genreArr = movieJson.getJSONArray("genre_ids");
            List<String> genreIds = new ArrayList<>();
            for(int i=0; i < genreArr.length(); ++i) {
                genreIds.add(genreArr.getInt(i) + "");
            }
            movie.setGenreIds(genreIds);
            return movie;
        }
        @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        try
        {   SharedPreferences preference= PreferenceManager.getDefaultSharedPreferences(context);
            String sort=preference.getString(ApplicationConstants.SORT_KEY,getResources().getString(R.string.str_sortPopularIndex));
            switch(sort)
            {   case "0":
                    sort="popularity.desc";
                    break;
                case "1":
                    sort="vote_average.desc";
                    break;
            }
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;

            for(int i = 1; i <= ApplicationConstants.NUM_PAGES_FETCH; ++i) {

                Uri.Builder reqUri = new Uri.Builder();
                reqUri.encodedPath(ApplicationConstants.API_URL);
                reqUri.appendQueryParameter("api_key", ApplicationConstants.API_KEY);
                reqUri.appendQueryParameter("sort_by", sort);
                reqUri.appendQueryParameter("page", i+"");

                URL reqUrl = new URL(reqUri.toString());
                urlConnection = (HttpsURLConnection) reqUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null)
                {
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                reader.close();
                urlConnection.disconnect();
                if (buffer.length() == 0) {
                    return null;
                }
                Movies = MovieList.get(context).getMovies();
                Movies.addAll(parseMovieData(buffer.toString()));

            }
        } catch (IOException e) {
        Log.e(TAG, "Failed to Fetch Data: ", e);
    } catch (JSONException e) {
        Log.e(TAG, "Failed to Parse Data: ", e);
    }

        return Movies;
    }
        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            super.onPostExecute(result);
            if(result!=null) {
                moviesAdapter = new MoviesAdapter(context, result);
                movieListRecyclerView.setAdapter(moviesAdapter);
            }
            else
            {   Toast.makeText(context,"Failed To Fetch Data",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
