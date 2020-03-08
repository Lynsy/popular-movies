package udacity.project.lynsychin.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import udacity.project.lynsychin.popularmovies.R;
import udacity.project.lynsychin.popularmovies.model.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Context mContext;
    private List<Trailer> mData = new ArrayList<>();
    private OnTrailerAdapterListener mListener;

    public interface OnTrailerAdapterListener {
        void onTrailerSelected(Trailer trailer);
    }

    public TrailerAdapter(Context context, OnTrailerAdapterListener listener){
        mContext = context;
        mListener = listener;
    }

    public void setData(List<Trailer> data){
        mData = data;
        notifyDataSetChanged();
    }

    public List<Trailer> getData() {
        return mData;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = mData.get(position);

        holder.tvName.setText(trailer.getName());
        holder.tvSite.setText(mContext.getString(R.string.siteLabel, trailer.getSite()));

        if(position == (mData.size()-1)){
            holder.separator.setVisibility(View.INVISIBLE);
        } else {
            holder.separator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView tvName;
        TextView tvSite;
        View separator;

        private TrailerViewHolder(View view){
            super(view);

            icon = view.findViewById(R.id.genericIcon);
            tvName = view.findViewById(R.id.tvName);
            tvSite = view.findViewById(R.id.tvSite);
            separator = view.findViewById(R.id.separator);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onTrailerSelected(mData.get(getAdapterPosition()));
            }
        }
    }

}
