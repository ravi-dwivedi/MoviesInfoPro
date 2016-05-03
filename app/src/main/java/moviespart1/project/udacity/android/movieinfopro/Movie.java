package moviespart1.project.udacity.android.movieinfopro;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Created by ravidwivedi on 25-04-2016.
 */
public class Movie implements Serializable, Comparable<Movie> {

    public static final String POSTER_BASE_URL =
            "http://image.tmdb.org/t/p";
    public static final String BACKDOOR_BASE_URL =
            "http://image.tmdb.org/t/p";
    private String backdoorSize = "w342";
    private String posterSize = "w500";
    private UUID mId;
    private String mTitle;
    private String posterPath;
    private boolean isAdult;
    private String overview;
    private String releaseDate;
    private List<String> genreIds;
    private String backdoorPath;
    private double popularity;
    private int voteCount;
    private double rating;
    private int sortBy;
    private boolean isFavourite;

    public Movie() {
        mId = UUID.randomUUID();
        sortBy = 1;
        isFavourite = false;
    }

    @Override
    public int compareTo(Movie rhs) {
        if(sortBy == 1) {   // Sort By Popularity
            if(this.popularity > rhs.popularity) {
                return -1;
            } else if(this.popularity < rhs.popularity) {
                return 1;
            }
        } else if(sortBy == 2) {    // Sort by Ratings
            if(this.rating > rhs.rating) {
                return -1;
            } else if(this.rating < rhs.rating) {
                return 1;
            }
        }
        return 0;
    }

    public String getSimpleTitle() {
        return mTitle;
    }

    public String getTitle() {
        String title = mTitle;
        if(mTitle.indexOf(':') != -1) {
            title = mTitle.replace(":", ":\n");
        } else if(mTitle.length() > 21) {
            int numSpaces = 0;
            for(int i = 0; i < title.length(); ++i) {
                if(title.charAt(i) == ' ')
                    numSpaces++;
            }

            numSpaces /= 2;
            for(int i = 0; i < title.length(); ++i) {

                if(title.charAt(i) == ' ') {
                    if(numSpaces == 0) {
                        title = title.substring(0, i) + '\n' + title.substring(i+1);
                        break;
                    } else {
                        numSpaces--;
                    }

                }
            }
        }

        return title;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPosterUrl() {
        String posterUrl = POSTER_BASE_URL + "/" +posterSize + "" + posterPath;
        return posterUrl;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean isAdult) {
        this.isAdult = isAdult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
    }

    public String getBackdoorPosterUrl() {
        String posterUrl = BACKDOOR_BASE_URL + "/" + backdoorSize + "" + backdoorPath;
        return posterUrl;
    }

    public void setBackdoorPath(String backdoorPath) {
        this.backdoorPath = backdoorPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setSortBy(int num) {
        sortBy = num;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }
}
