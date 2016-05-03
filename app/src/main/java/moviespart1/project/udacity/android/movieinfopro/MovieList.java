package moviespart1.project.udacity.android.movieinfopro;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ravidwivedi on 28-04-2016.
 */
public class MovieList {
    private static MovieList sMovieList;
    private static int sort_order = 0;
    private ArrayList<Movie> mMovies;
    private ArrayList<Movie> mFavouriteMovies;
    public static MovieList get(Context context) {
        if(sMovieList == null) {
            sMovieList = new MovieList(context);
        }
        return sMovieList;
    }

    private MovieList(Context context) {
        mMovies = new ArrayList<>();
        mFavouriteMovies = new ArrayList<>();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String sort = preference.getString(ApplicationConstants.SORT_KEY, "0");
        sort_order = Integer.parseInt(sort);
    }

    public ArrayList<Movie> getMovies() {
        return mMovies;
    }

    public ArrayList<Movie> getFavouriteMovies() {
        return mFavouriteMovies;
    }

    public void removeFromFavouriteMovies(Movie movie)
    {
        if(mFavouriteMovies.contains(movie)==true)
        {
            mFavouriteMovies.remove(movie);
        }
    }


    public void addIntoFavouriteMovies(Movie movie)
    {
        if(mFavouriteMovies.contains(movie)==false)
        {
            mFavouriteMovies.add(movie);
        }
    }

    public Movie getMovie(UUID id) {
        for(Movie movie: mMovies) {
            if(movie.getId().equals(id)) {
                return movie;
            }
        }
        return null;
    }

    public static int getSort_order()
    {
        return sort_order;
    }
    public static void setSort_order(int order)
    {
        sort_order = order;
    }
    public void addMovie(Movie movie) {
        mMovies.add(movie);
    }
    public void clearAll() {
        mMovies.clear();
    }

}
