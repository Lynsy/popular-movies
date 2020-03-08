package udacity.project.lynsychin.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import udacity.project.lynsychin.popularmovies.R;
import udacity.project.lynsychin.popularmovies.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context mContext;
    private List<Review> mData = new ArrayList<>();
    private OnReviewAdapterListener mListener;

    public interface OnReviewAdapterListener {
        void onReviewSelected(Review review);
    }

    public ReviewAdapter(Context context, OnReviewAdapterListener listener){
        mContext = context;
        mListener = listener;
    }

    public void setData(List<Review> data){
        mData = data;
        notifyDataSetChanged();
    }

    public List<Review> getData() {
        return mData;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mData.get(position);

        holder.tvContent.setText(mContext.getString(R.string.contentLabel, review.getContent()));
        holder.tvAuthor.setText(mContext.getString(R.string.authorLabel, review.getAuthor()));

        if(position % 2 == 0){
            holder.itemView.setBackgroundResource(R.color.white);
        } else {
            holder.itemView.setBackgroundResource(R.color.lightPurple);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContent;
        TextView tvAuthor;

        private ReviewViewHolder(View view){
            super(view);

            tvContent = view.findViewById(R.id.tvContent);
            tvAuthor = view.findViewById(R.id.tvAuthor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onReviewSelected(mData.get(getAdapterPosition()));
            }
        }
    }

}
