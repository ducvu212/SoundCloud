package com.framgia.music_24.screens.discover.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Track;
import java.util.List;

import static com.framgia.music_24.utils.StringUtils.isNotBlank;

/**
 * Created by CuD HniM on 18/08/24.
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private Context mContext;
    private List<Track> mTracks;
    private String mType;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;

    TrackAdapter(Context context, List<Track> tracks, String type, OnItemClickListener OnItemClickListener) {
        mContext = context;
        mType = type;
        mTracks = tracks;
        mListener = OnItemClickListener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View view = mInflater.inflate(R.layout.item_track, viewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder trackViewHolder, int i) {
        trackViewHolder.bindData(mContext, mTracks, mType, mListener);
    }

    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextViewName;
        private TextView mTextViewSinger;
        private ImageView mImageMusic;
        private String mType;
        private ConstraintLayout mLayout;
        private OnItemClickListener mListener;
        private List<Track> mTracks;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.textview_name_music);
            mTextViewSinger = itemView.findViewById(R.id.textview_name_singer);
            mImageMusic = itemView.findViewById(R.id.imageview_music);
            mLayout = itemView.findViewById(R.id.layout_item_track);
            mLayout.setOnClickListener(this);
        }

        void bindData(Context context, List<Track> tracks, String type,
                OnItemClickListener OnItemClickListener) {
            mTracks = tracks;
            mType = type;
            mTextViewName.setText(tracks.get(getAdapterPosition()).getTitle());
            mTextViewSinger.setText(tracks.get(getAdapterPosition()).getUser().getFullName());
            loadImage(context, mTracks);
            mListener = OnItemClickListener;
        }

        private void loadImage(Context context, List<Track> tracks) {
            if (tracks.get(getAdapterPosition()).getArtworkUrl() != null && isNotBlank(
                    tracks.get(getAdapterPosition()).getArtworkUrl())) {
                Glide.with(context)
                        .load(tracks.get(getAdapterPosition()).getArtworkUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_place_holder)
                                .error(R.drawable.ic_load_image_error))
                        .into(mImageMusic);
            }
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.OnTrackClick(mTracks, mType, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void OnTrackClick(List<Track> tracks, String type, int position);
    }
}
