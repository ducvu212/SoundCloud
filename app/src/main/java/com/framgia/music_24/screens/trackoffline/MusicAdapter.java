package com.framgia.music_24.screens.trackoffline;

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

import static android.view.View.GONE;

/**
 * Created by CuD HniM on 18/09/10.
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.TrackOfflineHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Track> mTracks;
    private OnItemClick mListener;

    MusicAdapter(Context context, List<Track> tracks,
            OnItemClick OnClickListener) {
        mContext = context;
        mTracks = tracks;
        mListener = OnClickListener;
    }

    @NonNull
    @Override
    public TrackOfflineHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View view = mInflater.inflate(R.layout.item_genre, viewGroup, false);
        return new TrackOfflineHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOfflineHolder favoriteHolder, int i) {
        favoriteHolder.bindData(mContext, mTracks, mListener);
    }

    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.size();
    }

    static class TrackOfflineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageViewAva;
        private ImageView mImageViewFavorite;
        private TextView mTextViewName;
        private TextView mTextViewSinger;
        private TextView mTextViewCreated;
        private TextView mTextViewDuration;
        private List<Track> mTracks;
        private ConstraintLayout mLayout;
        private OnItemClick mListener;

        TrackOfflineHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            mImageViewAva = itemView.findViewById(R.id.imageview_avatar);
            mImageViewFavorite = itemView.findViewById(R.id.imageview_favorite_genre);
            mTextViewCreated = itemView.findViewById(R.id.textview_created);
            mTextViewDuration = itemView.findViewById(R.id.textview_time);
            mTextViewName = itemView.findViewById(R.id.textview_name);
            mTextViewSinger = itemView.findViewById(R.id.textview_singer);
            mLayout = itemView.findViewById(R.id.layout_item_genre);
            mLayout.setOnClickListener(this);
            mImageViewFavorite.setOnClickListener(this);
        }

        private void bindData(Context context, List<Track> tracks,
                OnItemClick OnClickListener) {
            mTracks = tracks;
            mTextViewName.setText(tracks.get(getAdapterPosition()).getTitle());
            mTextViewSinger.setText(tracks.get(getAdapterPosition()).getUser().getUsername());
            mTextViewCreated.setVisibility(GONE);
            mTextViewDuration.setVisibility(GONE);
            loadImage(context, tracks.get(getAdapterPosition()));
            mImageViewFavorite.setVisibility(GONE);
            mListener = OnClickListener;
        }

        private void loadImage(Context context, Track track) {
            Glide.with(context)
                    .load(track.getBitmap())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_image_place_holder)
                            .error(R.drawable.ic_load_image_error))
                    .into(mImageViewAva);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                default:
                    mListener.OnItemOfflineClick(mTracks, getAdapterPosition());
            }
        }
    }

    public interface OnItemClick {
        void OnItemOfflineClick(List<Track> tracks, int position);
    }
}
