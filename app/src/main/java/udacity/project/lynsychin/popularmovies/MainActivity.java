package udacity.project.lynsychin.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import udacity.project.lynsychin.popularmovies.adapter.MovieAdapter;
import udacity.project.lynsychin.popularmovies.database.MovieEntry;
import udacity.project.lynsychin.popularmovies.model.Movie;
import udacity.project.lynsychin.popularmovies.model.MovieDB;
import udacity.project.lynsychin.popularmovies.network.APIClient;
import udacity.project.lynsychin.popularmovies.network.APIInterface;
import udacity.project.lynsychin.popularmovies.view_model.MainViewModel;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieAdapterListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SORT_TYPE_KEY = "sort_type_key";

    private static String mCurrentSort;
    private static boolean fetchFromNetwork = true;

    private MovieAdapter mAdapter;
    private APIInterface mApiInterface;

    private SwipeRefreshLayout mSRLMovies;
    private RecyclerView mRecyclerViewMovies;
    private TextView mTvErrorMessage;

    private MainViewModel mMainViewModel;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiInterface = APIClient.getClient().create(APIInterface.class);
        mMainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);

        mCurrentSort = getString(R.string.sort_popular);

        mTvErrorMessage = findViewById(R.id.tvErrorMessage);

        mSRLMovies = findViewById(R.id.srlMovies);
        mSRLMovies.setOnRefreshListener(this);

        mRecyclerViewMovies = findViewById(R.id.rvMovies);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerViewMovies.setAdapter(mAdapter);

        setViewModel();
        setupSharedPreferences();
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
        List<MovieEntry> movies = mAdapter.getData();
        if(item.getItemId() == R.id.action_popular){
            mCurrentSort = getString(R.string.sort_popular);
            saveSortingPreferences();
            sortMovies(movies);
            return true;
        } else if(item.getItemId() == R.id.action_topRated){
            mCurrentSort = getString(R.string.sort_top_rated);
            saveSortingPreferences();
            sortMovies(movies);
            return true;
        } else if(item.getItemId() == R.id.action_favorite){
            mCurrentSort = getString(R.string.sort_local_favorite);
            saveSortingPreferences();
            sortMovies(movies);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setViewModel(){
        mMainViewModel.getMovies().observe(MainActivity.this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(List<MovieEntry> movieEntries) {
                Log.d(TAG, "Loading from Database...");
                sortMovies(movieEntries);
            }
        });
    }

    private void setupSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentSort = mSharedPreferences.getString(SORT_TYPE_KEY, getString(R.string.sort_popular));
    }

    private void saveSortingPreferences(){
        if(mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(SORT_TYPE_KEY, mCurrentSort);
            editor.apply();
        }
    }

    private void displayList(){
        mSRLMovies.setRefreshing(false);
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
    }

    private void hideList(){
        mSRLMovies.setRefreshing(true);
        mRecyclerViewMovies.setVisibility(View.GONE);
    }

    private void displayErrorMessage(String error_message){
        mSRLMovies.setRefreshing(false);
        mRecyclerViewMovies.setVisibility(View.GONE);
        mTvErrorMessage.setVisibility(View.VISIBLE);

        mTvErrorMessage.setText(error_message);
    }

    private void hideErrorMessage(){
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mTvErrorMessage.setVisibility(View.GONE);
    }

    private void sortMovies(List<MovieEntry> movies){
        if(mCurrentSort.equals(getString(R.string.sort_popular))){
            Collections.sort(movies, new Comparator<MovieEntry>() {
                @Override
                public int compare(MovieEntry m1, MovieEntry m2) {
                    return Double.compare(m2.getPopularity(), m1.getPopularity());
                }
            });
        } else if(mCurrentSort.equals(getString(R.string.sort_top_rated))){
            Collections.sort(movies, new Comparator<MovieEntry>() {
                @Override
                public int compare(MovieEntry m1, MovieEntry m2) {
                    return Double.compare(m2.getVote_average(), m1.getVote_average());
                }
            });
        } else {
            Collections.sort(movies, new Comparator<MovieEntry>() {
                @Override
                public int compare(MovieEntry m1, MovieEntry m2) {
                    return Boolean.compare(m2.isFavorite(), m1.isFavorite());
                }
            });
        }

        mAdapter.setData(movies);
    }

    private void updateMovies(){
        hideList();
        hideErrorMessage();

        String currentSort = mCurrentSort;

        if(currentSort.equals(getString(R.string.sort_local_favorite))) currentSort = getString(R.string.sort_popular);

        Call<MovieDB> call = mApiInterface.getMovies(currentSort, getString(R.string.API_KEY));
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

                    displayList();
                } else {
                    if(mAdapter != null && mAdapter.getData().isEmpty()){
                        displayErrorMessage(getString(R.string.error_message_local_storage));
                    } else {
                        displayList();
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<MovieDB> call, @NonNull Throwable t) {
                call.cancel();

                if(mAdapter == null || mAdapter.getData().isEmpty()){
                    displayErrorMessage(getString(R.string.error_message));
                } else {
                    displayList();
                    Toast.makeText(MainActivity.this, getString(R.string.error_message_no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMovieSelected(MovieEntry movie) {
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        movieDetailIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        startActivity(movieDetailIntent);
    }

    @Override
    public void onRefresh() {
        updateMovies();
    }
}
