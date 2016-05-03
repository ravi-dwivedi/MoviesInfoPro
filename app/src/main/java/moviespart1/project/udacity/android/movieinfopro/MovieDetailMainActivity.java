package moviespart1.project.udacity.android.movieinfopro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by ravidwivedi on 29-04-2016.
 */
public class MovieDetailMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MovieDetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_list_main, menu);
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

    public static class MovieDetailFragment extends Fragment {

        private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

        private Movie movie;

        private CollapsingToolbarLayout mCollapsingToolbar;
        private Toolbar mToolbar;
        private ImageView mLandPosterView;
        private ImageView mPosterImageView;
        private TextView mTitleTextView;
        private TextView mRatingTextView;
        private RatingBar mRatingBar;
        private TextView mGenreTextView;
        private TextView mDateTextView;
        private TextView mVotesTextView;
        private TextView mDescTextView;
        private FloatingActionButton mFavouriteButton;
        public MovieDetailFragment()
        {
            setHasOptionsMenu(true);
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            Intent intent = getActivity().getIntent();
           // if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {

                UUID movieId = (UUID) intent.getSerializableExtra(ApplicationConstants.KEY_MOVIE);
                movie = MovieList.get(getActivity()).getMovie(movieId);
          //  }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);


            mLandPosterView  =(ImageView) rootView.findViewById(R.id.landPosterImageView);
            mPosterImageView = (ImageView) rootView.findViewById(R.id.posterImageView);
            mTitleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
            mRatingTextView = (TextView) rootView.findViewById(R.id.ratingTextView);
            mRatingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
            mGenreTextView= (TextView) rootView.findViewById(R.id.genreTextView);
            mDateTextView= (TextView) rootView.findViewById(R.id.dateTextView);
            mVotesTextView  = (TextView) rootView.findViewById(R.id.votesTextView);
            mDescTextView = (TextView) rootView.findViewById(R.id.descTextView);
            mFavouriteButton = (FloatingActionButton)rootView.findViewById(R.id.favButton);

            Picasso.with(getActivity()).load(movie.getBackdoorPosterUrl()).into(mLandPosterView);

            Picasso.with(getActivity()).load(movie.getPosterUrl()).into(mPosterImageView);

            String title = movie.getTitle();
            Spannable span = new SpannableString(title);
            int index = title.indexOf(':') + 1;
            if(index == 0) {
                index = title.length();
            }
            span.setSpan(new RelativeSizeSpan(1.25f), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTitleTextView.setText(span);

            double rating = movie.getRating();

            float stars = (float) (Math.round(rating * 10) / 20.0);
            mRatingBar.setRating(stars);

            rating = ((int) (rating*10))/10.0;
            String ratingText = rating + "/10";
            span = new SpannableString(ratingText);
            span.setSpan(new RelativeSizeSpan(1.8f), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mRatingTextView.setText(span);

            String genreText = "";
            int len = Math.min(movie.getGenreIds().size(), 3);
            for(int i=0; i < len; ++i) {
                String tmp = movie.getGenreIds().get(i);
                tmp = ApplicationConstants.getGenre(tmp);
                if(tmp != null) {
                    genreText = genreText + tmp;
                    if(i != len-1) {
                        genreText = genreText + ",\n";
                    }
                }
            }
            mGenreTextView.setText(genreText);

            mDateTextView.setText(movie.getReleaseDate());

            mVotesTextView.setText(movie.getVoteCount() + " Votes");

            span = new SpannableString(movie.getOverview());
            span.setSpan(new RelativeSizeSpan(1.5f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mDescTextView.setText(span);

            setUpFAB();
            mFavouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFav = !movie.isFavourite();
                    int resId;
                    if (isFav) {
                        resId = R.string.favorite;
                        MovieList.get(getActivity()).addIntoFavouriteMovies(movie);
                    } else {
                        resId = R.string.unfavorite;
                        MovieList.get(getActivity()).removeFromFavouriteMovies(movie);
                    }
                    Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
                    movie.setFavourite(isFav);
                    setUpFAB();
                }
            });

            if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mCollapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
                mCollapsingToolbar.setTitle(movie.getSimpleTitle());
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
                mCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);

                mToolbar = (Toolbar) rootView.findViewById(R.id.toolBar);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.setSupportActionBar(mToolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
//            mToolbar.setNavigationIcon(android.R.drawable.ic_action_back);
            }

            return rootView;
        }

        private void setUpFAB() {
            boolean isFav = movie.isFavourite();
            if(isFav) {
                mFavouriteButton.setImageResource(R.drawable.ic_favorite_red);
            } else {
                mFavouriteButton.setImageResource(R.drawable.ic_favorite_white);
            }
        }


    }
}