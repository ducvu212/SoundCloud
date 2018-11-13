package com.framgia.music_24.screens.playinglist;

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

import static com.framgia.music_24.screens.play.PlayMusicFragment.PLAY_FAVORITE;

/**
 * Created by CuD HniM on 18/09/07.
 */
public class PlayingListAdapter extends RecyclerView.Adapter<PlayingListAdapter.PlayListHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Track> mTracks;
    private String mName;
    private GenreAdapter.OnClickListener mListener;

    PlayingListAdapter(Context context, List<Track> tracks, String name,
            GenreAdapter.OnClickListener OnClickListener) {
        mContext = context;
        mTracks = tracks;
        mName = name;
        mListener = OnClickListener;
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View view = mInflater.inflate(R.layout.item_genre, viewGroup, false);
        return new PlayListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder playListHolder, int i) {
        playListHolder.bindData(mContext, mTracks, mName, mListener);
    }

    @Override
    public int getItemCount() {
        return mTracks == null ? 0 : mTracks.size();
    }

    static class PlayListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageViewAva;
        private ImageView mImageViewFavorite;
        private TextView mTextViewName;
        private TextView mTextViewSinger;
        private TextView mTextViewCreated;
        private TextView mTextViewDuration;
        private List<Track> mTracks;
        private ConstraintLayout mLayout;
        private GenreAdapter.OnClickListener mListener;

        PlayListHolder(@NonNull View itemView) {
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

        private void bindData(Context context, List<Track> tracks, String name,
                GenreAdapter.OnClickListener OnClickListener) {
            mTracks = tracks;
            if (mTracks.get(getAdapterPosition()).getTitle().equals(name)) {
                mTextViewName.setTextColor(
                        context.getResources().getColor(R.color.color_deep_orange_accent_400));
                mTextViewSinger.setTextColor(
                        context.getResources().getColor(R.color.color_deep_orange_accent_400));
            } else {
                mTextViewName.setTextColor(
                        context.getResources().getColor(R.color.color_black_alpha_222));
                mTextViewSinger.setTextColor(
                        context.getResources().getColor(R.color.color_black_alpha_222));
            }
            mTextViewName.setText(tracks.get(getAdapterPosition()).getTitle());
            mTextViewSinger.setText(tracks.get(getAdapterPosition()).getUser().getUsername());
            if (tracks.get(getAdapterPosition()).getCreatedAt() != null) {
                mTextViewCreated.setText(
                        StringUtils.convertTime(tracks.get(getAdapterPosition()).getCreatedAt()));
            }
            mTextViewDuration.setText(StringUtils.convertMilisecToMinute(
                    tracks.get(getAdapterPosition()).getFullDuration()));
            loadImage(context, tracks.get(getAdapterPosition()));
            loadFavorite(tracks.get(getAdapterPosition()).getFavorite());
            mListener = OnClickListener;
        }

        private void loadFavorite(int favorite) {
            if (favorite == PLAY_FAVORITE) {
                mImageViewFavorite.setImageResource(R.drawable.ic_favorite);
            } else {
                mImageViewFavorite.setImageResource(R.drawable.ic_un_favorite);
            }
        }

        private void loadImage(Context context, Track track) {
            if (!track.isOffline()) {
                Glide.with(context)
                        .load(track.getArtworkUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_place_holder)
                                .error(R.drawable.ic_load_image_error))
                        .into(mImageViewAva);
            } else {
                Glide.with(context)
                        .load(track.getBitmap())
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_place_holder)
                                .error(R.drawable.ic_load_image_error))
                        .into(mImageViewAva);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.imageview_favorite_genre:
                    mListener.OnFavoriteClick(mTracks, getAdapterPosition());
                    break;

                default:
                    mListener.OnItemClick(mTracks, getAdapterPosition());
            }
        }
    }
}
