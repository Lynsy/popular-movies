package udacity.project.lynsychin.popularmovies.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

import udacity.project.lynsychin.popularmovies.model.Movie;

@Entity(tableName = "movie")
public class MovieEntry {

    @PrimaryKey
    private int id;
    private double popularity;
    private int voteCount;
    private boolean video;
    private String posterPath;
    private boolean adult;
    private String backdropPath;
    private String originalLanguage;
    private String originalTitle;
    private List<Integer> genreIds;
    private String title;
    private double vote_average;
    private String overview;
    private String releaseDate; //Expected Format yyyy-mm-dd

    //for local use...
    private boolean favorite;

    public MovieEntry(int id, double popularity, int voteCount, boolean video, String posterPath, boolean adult,
                      String backdropPath, String originalLanguage, String originalTitle, List<Integer> genreIds,
                      String title, double vote_average, String overview, String releaseDate, boolean favorite) {
        this.id = id;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.video = video;
        this.posterPath = posterPath;
        this.adult = adult;
        this.backdropPath = backdropPath;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.genreIds = genreIds;
        this.title = title;
        this.vote_average = vote_average;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.favorite = favorite;
    }

    @Ignore
    public MovieEntry(Movie movie) {
        this.id = movie.getId();
        this.popularity = movie.getPopularity();
        this.voteCount = movie.getVoteCount();
        this.video = movie.getVideo();
        this.posterPath = movie.getPosterPath();
        this.adult = movie.getAdult();
        this.backdropPath = movie.getBackdropPath();
        this.originalLanguage = movie.getOriginalLanguage();
        this.originalTitle = movie.getOriginalTitle();
        this.genreIds = movie.getGenreIds();
        this.title = movie.getTitle();
        this.vote_average = movie.getVote_average();
        this.overview = movie.getOverview();
        this.releaseDate = movie.getReleaseDate();
        this.favorite = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
