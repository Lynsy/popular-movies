package udacity.project.lynsychin.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import udacity.project.lynsychin.popularmovies.database.MovieDatabase;
import udacity.project.lynsychin.popularmovies.database.MovieEntry;
import udacity.project.lynsychin.popularmovies.model.Movie;
import udacity.project.lynsychin.popularmovies.model.MovieDB;
import udacity.project.lynsychin.popularmovies.network.APIClient;
import udacity.project.lynsychin.popularmovies.network.APIInterface;
import udacity.project.lynsychin.popularmovies.view_model.MainViewModel;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieAdapterListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static String mCurrentSort;
    private static boolean fetchFromNetwork = true;

    private MovieAdapter mAdapter;
    private APIInterface mApiInterface;

    private ProgressBar mPbLoading;
    private RecyclerView mRecyclerViewMovies;
    private TextView mTvErrorMessage;

    private MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiInterface = APIClient.getClient().create(APIInterface.class);
        mMainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);

        mCurrentSort = getString(R.string.sort_popular);

        mPbLoading = findViewById(R.id.pbLoading);
        mTvErrorMessage = findViewById(R.id.tvErrorMessage);

        mRecyclerViewMovies = findViewById(R.id.rvMovies);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerViewMovies.setAdapter(mAdapter);

        setViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fetchFromNetwork) {
            fetchFromNetwork = false;
            updateMovies();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_popular){
            mMainViewModel.updateSelectedSort(getString(R.string.sort_local_popularity));
            return true;
        } else if(item.getItemId() == R.id.action_topRated){
            mMainViewModel.updateSelectedSort(getString(R.string.sort_local_highest_rated));
            return true;
        } else if(item.getItemId() == R.id.action_favorite){
            // TODO: Add new category for favorite
            // Update by highest_rating for all movies marked as favorite
            // Or movies marked as favorite on top...

            Toast.makeText(this, "Under Construction", Toast.LENGTH_SHORT).show();
        }

        // TODO: Refresh list
        return super.onOptionsItemSelected(item);
    }

    private void setViewModel(){
        mMainViewModel.getMovies().observe(MainActivity.this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(List<MovieEntry> movieEntries) {
                Log.d(TAG, "Loading from Database...");
                mAdapter.setData(movieEntries);
            }
        });
    }

    private void displayList(){
        mPbLoading.setVisibility(View.GONE);
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
    }

    private void hideList(){
        mPbLoading.setVisibility(View.VISIBLE);
        mRecyclerViewMovies.setVisibility(View.GONE);
    }

    private void displayErrorMessage(String error_message){
        mRecyclerViewMovies.setVisibility(View.GONE);
        mTvErrorMessage.setVisibility(View.VISIBLE);

        mTvErrorMessage.setText(error_message);
    }

    private void hideErrorMessage(){
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mTvErrorMessage.setVisibility(View.GONE);
    }

    // Should now call the API to insert new movies into the local db
    private void updateMovies(){
        hideList();
        Call<MovieDB> call = mApiInterface.getMovies(mCurrentSort, getString(R.string.API_KEY));
        call.enqueue(new Callback<MovieDB>() {
            @Override
            public void onResponse(@NonNull Call<MovieDB> call, @NonNull Response<MovieDB> response) {
                Log.d(TAG, response.code()+"");

                MovieDB db = response.body();
                if(db != null && db.getMovies() != null){
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            for(Movie newMovie : db.getMovies()) {
                                if (!mMainViewModel.hasMovie(newMovie.getId())) {
                                    MovieEntry movie = new MovieEntry(newMovie);
                                    mMainViewModel.addMovie(movie);
                                }
                            }
                        }
                    });
                }

                displayList();
            }

            @Override
            public void onFailure(@NonNull Call<MovieDB> call, @NonNull Throwable t) {
                call.cancel();
                displayList();

                // TODO: I need to make sure to display an appropriated error message
                // Scenario 1 : No connection to server (due to internet connectivity issues AND the DB is empty
                // Display an error to Network problem in the internet

                // If there's a connectivity issue, but there's data in the DB
                // Display current movies, but display Toast message to update when there's internet.

                //displayErrorMessage(getString(R.string.error_message));


            }
        });
    }

    @Override
    public void onMovieSelected(MovieEntry movie) {
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        movieDetailIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        startActivity(movieDetailIntent);
    }
}
