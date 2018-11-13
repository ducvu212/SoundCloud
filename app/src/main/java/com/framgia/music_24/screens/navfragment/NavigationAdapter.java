package com.framgia.music_24.screens.navfragment;

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
import com.framgia.music_24.screens.genre.GenreAdapter;
import com.framgia.music_24.utils.StringUtils;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by CuD HniM on 18/09/08.
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.FavoriteHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Track> mTracks;
    private GenreAdapter.OnClickListener mListener;
    private OnLongClickListener mLongClickListener;

    NavigationAdapter(Context context, List<Track> tracks,
            GenreAdapter.OnClickListener OnClickListener, OnLongClickListener onLongClickListener) {
        mContext = context;
        mTracks = tracks;
        mListener = OnClickListener;
        mLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View view = mInflater.inflate(R.layout.item_genre, viewGroup, false);
        return new FavoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder favoriteHolder, int i) {
        favoriteHolder.bindData(mContext, mTracks, mListener, mLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.size();
    }

    static class FavoriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private ImageView mImageViewAva;
        private ImageView mImageViewFavorite;
        private TextView mTextViewName;
        private TextView mTextViewSinger;
        private TextView mTextViewCreated;
        private TextView mTextViewDuration;
        private List<Track> mTracks;
        private ConstraintLayout mLayout;
        private GenreAdapter.OnClickListener mListener;
        private OnLongClickListener mLongClickListener;

        FavoriteHolder(@NonNull View itemView) {
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
            itemView.setOnLongClickListener(this);
        }

        private void bindData(Context context, List<Track> tracks,
                GenreAdapter.OnClickListener OnClickListener, OnLongClickListener onLongClickListener) {
            mTracks = tracks;
            mTextViewName.setText(tracks.get(getAdapterPosition()).getTitle());
            mTextViewSinger.setText(tracks.get(getAdapterPosition()).getUser().getUsername());
            mTextViewCreated.setText(tracks.get(getAdapterPosition()).getCreatedAt());
            mTextViewDuration.setText(StringUtils.convertMilisecToMinute(
                    tracks.get(getAdapterPosition()).getFullDuration()));
            loadImage(context, tracks.get(getAdapterPosition()));
            mImageViewFavorite.setVisibility(GONE);
            mListener = OnClickListener;
            mLongClickListener = onLongClickListener;
        }

        private void loadImage(Context context, Track track) {
            Glide.with(context)
                    .load(track.getArtworkUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_image_place_holder)
                            .error(R.drawable.ic_load_image_error))
                    .into(mImageViewAva);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                default:
                    mListener.OnItemClick(mTracks, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mLongClickListener.OnLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnLongClickListener {
        void OnLongClick(int position);
    }
}
