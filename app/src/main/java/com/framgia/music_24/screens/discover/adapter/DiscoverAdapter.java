package com.framgia.music_24.screens.discover.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.framgia.music_24.R;
import com.framgia.music_24.data.model.Discover;
import com.framgia.music_24.data.model.Track;
import java.util.List;

/**
 * Created by CuD HniM on 18/08/24.
 */
public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverHolder> {

    private Context mContext;
    private List<Discover> mDiscovers;
    private LayoutInflater mInflater;
    private OnClickListener mListener;
    private TrackAdapter.OnItemClickListener mTrackClickListener;

    public DiscoverAdapter(Context context, List<Discover> discovers,
            OnClickListener OnClickListener, TrackAdapter.OnItemClickListener OnItemClickListener) {
        mContext = context;
        mDiscovers = discovers;
        mListener = OnClickListener;
        mTrackClickListener = OnItemClickListener;
    }

    @NonNull
    @Override
    public DiscoverHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View view = mInflater.inflate(R.layout.item_discover_gender, viewGroup, false);
        return new DiscoverHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverHolder discoverHolder, int i) {
        discoverHolder.bindData(mContext, mDiscovers.get(i), mListener);
    }

    @Override
    public int getItemCount() {
        return mDiscovers == null ? 0 : mDiscovers.size();
    }

    public class DiscoverHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextViewGender;
        private RecyclerView mRecyclerGender;
        private OnClickListener mListener;
        private String mGenre;

        DiscoverHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewGender = itemView.findViewById(R.id.textview_gender);
            mRecyclerGender = itemView.findViewById(R.id.recycler_gender);
        }

        void bindData(Context context, Discover discover, OnClickListener OnClickListener) {
            mTextViewGender.setText(discover.getGender());
            mGenre = discover.getGender();
            mTextViewGender.setOnClickListener(this);
            initRecycleGenders(context, discover);
            mListener = OnClickListener;
        }

        private void initRecycleGenders(Context context, Discover discover) {
            List<Track> tracks = discover.getTracks();
            TrackAdapter adapter = new TrackAdapter(context, tracks, discover.getType(), mTrackClickListener);
            mRecyclerGender.setHasFixedSize(true);
            mRecyclerGender.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerGender.setAdapter(adapter);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.textview_gender:
                    if (mListener != null) {
                        mListener.OnGenreClick(getAdapterPosition(), mGenre);
                    }
                    break;
            }
        }
    }

    public interface OnClickListener {
        void OnGenreClick(int position, String title);
    }
}
