package udacity.project.lynsychin.popularmovies;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import udacity.project.lynsychin.popularmovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView ivPoster = findViewById(R.id.detailMoviePoster);
        TextView tvReleaseDate = findViewById(R.id.detailMovieReleaseDate);
        TextView tvRating = findViewById(R.id.detailMovieRating);
        TextView tvTitle = findViewById(R.id.detailMovieTitle);
        TextView tvPlot = findViewById(R.id.detailMoviePlot);

        Movie selectedMovie;
        if(getIntent().hasExtra("movie")){
            selectedMovie = (Movie) getIntent().getSerializableExtra("movie");

            tvTitle.setText(selectedMovie.getTitle());

            Picasso.get()
                    .load(getString(R.string.base_url_poster_path_large) + selectedMovie.getPosterPath())
                    .into(ivPoster);

            tvReleaseDate.setText(getString(R.string.releaseLabel, parseDateFormat(selectedMovie.getReleaseDate())));
            tvRating.setText(getString(R.string.ratingLabel, String.valueOf(selectedMovie.getVote_average())));
            tvPlot.setText(selectedMovie.getOverview());
        } else {
            tvPlot.setText(getString(R.string.error_on_movie_fetch));
        }

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
