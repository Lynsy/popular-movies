package udacity.project.lynsychin.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import udacity.project.lynsychin.popularmovies.adapter.TrailerAdapter;
import udacity.project.lynsychin.popularmovies.model.Trailer;
import udacity.project.lynsychin.popularmovies.model.TrailerDB;
import udacity.project.lynsychin.popularmovies.network.APIClient;
import udacity.project.lynsychin.popularmovies.network.APIInterface;

public class TrailerListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, TrailerAdapter.OnTrailerAdapterListener {

    private static final String TAG = TrailerListActivity.class.getSimpleName();
    private static final int DEFAULT_MOVIE_ID = -1;

    private SwipeRefreshLayout mSRLTrailers;
    private RecyclerView mRecyclerViewTrailers;
    private TrailerAdapter mAdapter;
    private TextView tvErrorMessage;

    private APIInterface mApiInterface;
    private int mMovieId = DEFAULT_MOVIE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_list);

        if(getIntent().hasExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE)){
            String title = getIntent().getStringExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE);
            setTitle(title);
        }

        mApiInterface = APIClient.getClient().create(APIInterface.class);

        tvErrorMessage = findViewById(R.id.tvErrorMessage);

        mSRLTrailers = findViewById(R.id.srlTrailers);
        mSRLTrailers.setOnRefreshListener(this);

        mRecyclerViewTrailers = findViewById(R.id.rvTrailers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerViewTrailers.setLayoutManager(layoutManager);
        mRecyclerViewTrailers.setHasFixedSize(true);

        mAdapter = new TrailerAdapter(this, this);
        mRecyclerViewTrailers.setAdapter(mAdapter);

        if(getIntent().hasExtra(MovieDetailActivity.EXTRA_MOVIE_ID)){
            mMovieId = getIntent().getIntExtra(MovieDetailActivity.EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
            fetchTrailers();
        }
    }

    private void fetchTrailers(){
        hideErrorMessage();
        mSRLTrailers.setRefreshing(true);
        Call<TrailerDB> call = mApiInterface.getMovieTrailerById(mMovieId, getString(R.string.API_KEY));
        call.enqueue(new Callback<TrailerDB>() {
            @Override
            public void onResponse(@NonNull Call<TrailerDB> call, @NonNull Response<TrailerDB> response) {
                Log.d(TAG, response.code()+"");

                TrailerDB db = response.body();
                if(db != null && db.getTrailers() != null){
                    mAdapter.setData(db.getTrailers());

                    if(mAdapter.getData().isEmpty()){
                        displayErrorMessage(getString(R.string.error_message_no_trailers, getTitle()));
                    }
                } else {
                    displayErrorMessage(getString(R.string.error_message_no_trailers, getTitle()));
                }
                mSRLTrailers.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<TrailerDB> call, @NonNull Throwable t) {
                call.cancel();
                mSRLTrailers.setRefreshing(false);
                displayErrorMessage(getString(R.string.error_message));
            }
        });
    }

    private void displayErrorMessage(String error_message){
        mRecyclerViewTrailers.setVisibility(View.GONE);
        tvErrorMessage.setVisibility(View.VISIBLE);

        tvErrorMessage.setText(error_message);
    }

    private void hideErrorMessage(){
        mRecyclerViewTrailers.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.GONE);
    }

    private void watchTrailer(String key){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));

        if (appIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(appIntent);
        } else {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + key));
            startActivity(webIntent);
        }
    }

    @Override
    public void onRefresh() {
        if(mMovieId != DEFAULT_MOVIE_ID){
            fetchTrailers();
        }
    }

    @Override
    public void onTrailerSelected(Trailer trailer) {
        watchTrailer(trailer.getKey());
    }
}
