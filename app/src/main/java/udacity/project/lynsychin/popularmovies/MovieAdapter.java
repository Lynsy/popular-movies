package udacity.project.lynsychin.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import udacity.project.lynsychin.popularmovies.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private List<Movie> mData = new ArrayList<>();
    private OnMovieAdapterListener mListener;

    interface OnMovieAdapterListener {
        void onMovieSelected(Movie movie);
    }

    MovieAdapter(Context context, OnMovieAdapterListener listener){
        mContext = context;
        mListener = listener;
    }

    void setData(List<Movie> data){
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_movies, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mData.get(position);

        Picasso.get()
                .load(mContext.getString(R.string.base_url_poster_path) + movie.getPosterPath())
                .into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView posterImage;

        private MovieViewHolder(View view){
            super(view);

            posterImage = view.findViewById(R.id.posterImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onMovieSelected(mData.get(getAdapterPosition()));
            }
        }
    }

}
