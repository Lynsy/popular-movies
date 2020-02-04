package udacity.project.lynsychin.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import udacity.project.lynsychin.popularmovies.model.Movie;
import udacity.project.lynsychin.popularmovies.model.MovieDB;
import udacity.project.lynsychin.popularmovies.network.APIClient;
import udacity.project.lynsychin.popularmovies.network.APIInterface;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieAdapterListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static String mCurrentSort;

    private MovieAdapter mAdapter;
    private APIInterface mApiInterface;

    private ProgressBar mPbLoading;
    private RecyclerView mRecyclerViewMovies;
    private TextView mTvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiInterface = APIClient.getClient().create(APIInterface.class);

        mCurrentSort = getString(R.string.sort_popular);

        mPbLoading = findViewById(R.id.pbLoading);
        mTvErrorMessage = findViewById(R.id.tvErrorMessage);

        mRecyclerViewMovies = findViewById(R.id.rvMovies);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mAdapter = new MovieAdapter(MainActivity.this, this);
        mRecyclerViewMovies.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_popular){
            mCurrentSort = getString(R.string.sort_popular);
            updateMovies();
            return true;
        } else if(item.getItemId() == R.id.action_topRated){
            mCurrentSort = getString(R.string.sort_top_rated);
            updateMovies();
            return true;
        } else if(item.getItemId() == R.id.action_refresh){
            hideErrorMessage();
            updateMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayList(){
        mPbLoading.setVisibility(View.GONE);
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
    }

    private void hideList(){
        mPbLoading.setVisibility(View.VISIBLE);
        mRecyclerViewMovies.setVisibility(View.GONE);
    }

    private void displayErrorMessage(){
        mRecyclerViewMovies.setVisibility(View.GONE);
        mTvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage(){
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mTvErrorMessage.setVisibility(View.GONE);
    }

    private void updateMovies(){
        hideList();
        Call<MovieDB> call = mApiInterface.getMovies(mCurrentSort, getString(R.string.API_KEY));
        call.enqueue(new Callback<MovieDB>() {
            @Override
            public void onResponse(@NonNull Call<MovieDB> call, @NonNull Response<MovieDB> response) {
                Log.d(TAG, response.code()+"");

                MovieDB db = response.body();

                if(db != null && db.getMovies() != null)
                    mAdapter.setData(db.getMovies());

                displayList();
            }

            @Override
            public void onFailure(@NonNull Call<MovieDB> call, @NonNull Throwable t) {
                call.cancel();
                displayList();
                displayErrorMessage();
            }
        });
    }

    @Override
    public void onMovieSelected(Movie movie) {
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        movieDetailIntent.putExtra("movie", movie);
        startActivity(movieDetailIntent);
    }
}
