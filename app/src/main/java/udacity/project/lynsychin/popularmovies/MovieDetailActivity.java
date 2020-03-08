package udacity.project.lynsychin.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import udacity.project.lynsychin.popularmovies.database.MovieDatabase;
import udacity.project.lynsychin.popularmovies.database.MovieEntry;
import udacity.project.lynsychin.popularmovies.view_model.MovieDetailViewModel;
import udacity.project.lynsychin.popularmovies.view_model.MovieDetailViewModelFactory;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_MOVIE_ID = "extraMovieId";
    public static final String EXTRA_MOVIE_TITLE = "extraMovieTitle";
    private static final int DEFAULT_MOVIE_ID = -1;

    private static int mMovieId = DEFAULT_MOVIE_ID;
    private MovieEntry mMovie;

    private boolean isFavorite = false;

    private ImageView mIVPoster;
    private TextView mTVReleaseDate;
    private TextView mTVRating;
    private TextView mTVTitle;
    private TextView mTVPlot;
    private ImageView mOpenTrailer, mOpenReview;
    private TextView mLblOpenTrailer, mLblOpenReview;
    private MovieDetailViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_2);

        initializeLayout();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent().hasExtra(EXTRA_MOVIE_ID)){
            mMovieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
            setViewModel();
        } else if(mMovieId != DEFAULT_MOVIE_ID) {
            setViewModel();
        } else {
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
            isFavorite = !isFavorite;

            if(isFavorite){
                item.setIcon(R.drawable.ic_star_24dp);
            } else {
                item.setIcon(R.drawable.ic_star_border_24dp);
            }
            
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mViewModel.updateMovie(isFavorite);
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setViewModel(){
        MovieDatabase db = MovieDatabase.getInstance(this);
        MovieDetailViewModelFactory factory = new MovieDetailViewModelFactory(db, mMovieId);

        mViewModel = new ViewModelProvider(this, factory).get(MovieDetailViewModel.class);
        mViewModel.getMovie().observe(MovieDetailActivity.this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(MovieEntry movieEntry) {
                mViewModel.getMovie().removeObserver(this);
                populateUI(movieEntry);
            }
        });
    }

    private void initializeLayout(){
        mIVPoster = findViewById(R.id.detailMoviePoster);
        mTVReleaseDate = findViewById(R.id.detailMovieReleaseDate);
        mTVRating = findViewById(R.id.detailMovieRating);
        mTVTitle = findViewById(R.id.detailMovieTitle);
        mTVPlot = findViewById(R.id.detailMoviePlot);
        mOpenTrailer = findViewById(R.id.openTrailer);
        mLblOpenTrailer = findViewById(R.id.lblOpenTrailer);
        mOpenReview = findViewById(R.id.openReviews);
        mLblOpenReview = findViewById(R.id.lblOpenReview);
    }

    private void setListeners(){
        mOpenTrailer.setOnClickListener(this);
        mLblOpenTrailer.setOnClickListener(this);

        mOpenReview.setOnClickListener(this);
        mLblOpenReview.setOnClickListener(this);
    }

    private void populateUI(MovieEntry movie){
        mMovie = movie;
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.openTrailer || v.getId() == R.id.lblOpenTrailer){
            // Open Trailer List
            Intent openTrailersIntent = new Intent(this, TrailerListActivity.class);
            openTrailersIntent.putExtra(EXTRA_MOVIE_ID, mMovieId);
            openTrailersIntent.putExtra(EXTRA_MOVIE_TITLE, mTVTitle.getText().toString());
            startActivity(openTrailersIntent);
        } else if(v.getId() == R.id.openReviews || v.getId() == R.id.lblOpenReview){
            // Open Review List
            Intent openReviewsIntent = new Intent(this, ReviewsActivity.class);
            openReviewsIntent.putExtra(EXTRA_MOVIE_ID, mMovieId);
            openReviewsIntent.putExtra(EXTRA_MOVIE_TITLE, mTVTitle.getText().toString());
            startActivity(openReviewsIntent);
        }
    }
}
