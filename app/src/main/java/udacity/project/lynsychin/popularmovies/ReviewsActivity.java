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
import udacity.project.lynsychin.popularmovies.adapter.ReviewAdapter;
import udacity.project.lynsychin.popularmovies.model.Review;
import udacity.project.lynsychin.popularmovies.model.ReviewDB;
import udacity.project.lynsychin.popularmovies.network.APIClient;
import udacity.project.lynsychin.popularmovies.network.APIInterface;

public class ReviewsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ReviewAdapter.OnReviewAdapterListener {

    private static final String TAG = ReviewsActivity.class.getSimpleName();
    private static final int DEFAULT_MOVIE_ID = -1;

    private SwipeRefreshLayout mSRLReview;
    private RecyclerView mRecyclerViewReview;
    private ReviewAdapter mAdapter;
    private TextView tvErrorMessage;

    private APIInterface mApiInterface;
    private int mMovieId = DEFAULT_MOVIE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        if(getIntent().hasExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE)){
            String title = getIntent().getStringExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE);
            setTitle(title);
        }

        mApiInterface = APIClient.getClient().create(APIInterface.class);

        tvErrorMessage = findViewById(R.id.tvErrorMessage);

        mSRLReview = findViewById(R.id.srlReviews);
        mSRLReview.setOnRefreshListener(this);

        mRecyclerViewReview = findViewById(R.id.rvReviews);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerViewReview.setLayoutManager(layoutManager);
        mRecyclerViewReview.setHasFixedSize(true);

        mAdapter = new ReviewAdapter(this, this);
        mRecyclerViewReview.setAdapter(mAdapter);

        if(getIntent().hasExtra(MovieDetailActivity.EXTRA_MOVIE_ID)){
            mMovieId = getIntent().getIntExtra(MovieDetailActivity.EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
            fetchReviews();
        }
    }

    @Override
    public void onRefresh() {
        if(mMovieId != DEFAULT_MOVIE_ID){
            fetchReviews();
        }
    }

    @Override
    public void onReviewSelected(Review review) {
        Uri webPage = Uri.parse(review.getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void displayErrorMessage(String error_message){
        mRecyclerViewReview.setVisibility(View.GONE);
        tvErrorMessage.setVisibility(View.VISIBLE);

        tvErrorMessage.setText(error_message);
    }

    private void hideErrorMessage(){
        mRecyclerViewReview.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.GONE);
    }

    private void fetchReviews(){
        hideErrorMessage();
        mSRLReview.setRefreshing(true);
        Call<ReviewDB> call = mApiInterface.getMovieReviewsById(mMovieId, getString(R.string.API_KEY));
        call.enqueue(new Callback<ReviewDB>() {
            @Override
            public void onResponse(@NonNull Call<ReviewDB> call, @NonNull Response<ReviewDB> response) {
                Log.d(TAG, response.code()+"");

                ReviewDB db = response.body();
                if(db != null && db.getReviews() != null){
                    mAdapter.setData(db.getReviews());

                    if(mAdapter.getData().isEmpty()){
                        displayErrorMessage(getString(R.string.error_message_no_reviews, getTitle()));
                    }
                } else {
                    displayErrorMessage(getString(R.string.error_message_no_reviews, getTitle()));
                }
                mSRLReview.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ReviewDB> call, @NonNull Throwable t) {
                call.cancel();
                mSRLReview.setRefreshing(false);
                displayErrorMessage(getString(R.string.error_message));
            }
        });
    }
}
