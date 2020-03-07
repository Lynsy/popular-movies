package udacity.project.lynsychin.popularmovies;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import udacity.project.lynsychin.popularmovies.database.MovieDatabase;
import udacity.project.lynsychin.popularmovies.database.MovieEntry;
import udacity.project.lynsychin.popularmovies.model.Movie;
import udacity.project.lynsychin.popularmovies.view_model.MainViewModel;
import udacity.project.lynsychin.popularmovies.view_model.MovieDetailViewModel;
import udacity.project.lynsychin.popularmovies.view_model.MovieDetailViewModelFactory;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "extraTaskId";
    private static final int DEFAULT_MOVIE_ID = -1;

    private MovieDatabase mDB;
    private int mMovieId = DEFAULT_MOVIE_ID;

    private boolean isFavorite = false;

    private ImageView mIVPoster;
    private TextView mTVReleaseDate;
    private TextView mTVRating;
    private TextView mTVTitle;
    private TextView mTVPlot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initializeLayout();

        mDB = MovieDatabase.getInstance(this);

        if(getIntent().hasExtra(EXTRA_MOVIE_ID)){
            mMovieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);

            MovieDetailViewModelFactory factory = new MovieDetailViewModelFactory(mDB, mMovieId);

            final MovieDetailViewModel viewModel = new ViewModelProvider(this, factory).get(MovieDetailViewModel.class);
            viewModel.getMovie().observe(MovieDetailActivity.this, new Observer<MovieEntry>() {
                @Override
                public void onChanged(MovieEntry movieEntry) {
                    viewModel.getMovie().removeObserver(this);
                    populateUI(movieEntry);
                }
            });
        } else {
            // TODO: Put a nicer empty/error message
            mTVPlot.setText(getString(R.string.error_on_movie_fetch));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);

        MenuItem markAsFav = menu.findItem(R.id.action_mark_favorite);

        if(isFavorite){
            markAsFav.setIcon(R.drawable.ic_star_24dp);
        } else {
            markAsFav.setIcon(R.drawable.ic_star_border_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_mark_favorite){
            Toast.makeText(this, "Under Construction", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeLayout(){
        mIVPoster = findViewById(R.id.detailMoviePoster);
        mTVReleaseDate = findViewById(R.id.detailMovieReleaseDate);
        mTVRating = findViewById(R.id.detailMovieRating);
        mTVTitle = findViewById(R.id.detailMovieTitle);
        mTVPlot = findViewById(R.id.detailMoviePlot);
    }

    private void populateUI(MovieEntry movie){
        mTVTitle.setText(movie.getTitle());

        Picasso.get()
                .load(getString(R.string.base_url_poster_path_large) + movie.getPosterPath())
                .into(mIVPoster);

        mTVReleaseDate.setText(getString(R.string.releaseLabel, parseDateFormat(movie.getReleaseDate())));
        mTVRating.setText(getString(R.string.ratingLabel, String.valueOf(movie.getVote_average())));
        mTVPlot.setText(movie.getOverview());

        isFavorite = movie.isFavorite();
    }

    /**
     * So the date can be more readable
     * @param releaseDate movie's release date (yyyy-MM-dd)
     * @return a new format of the same release date (MMMM dd, yyyy)
     */
    private String parseDateFormat(String releaseDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date();
        try {
            date = format.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        return newDateFormat.format(date);
    }
}
