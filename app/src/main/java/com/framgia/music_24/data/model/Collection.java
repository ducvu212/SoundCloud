package com.framgia.music_24.data.model;

/**
 * Created by CuD HniM on 18/08/24.
 */
public class Collection {

    private Track mTrack;
    private Double mScore;

    public Collection(CollectionBuilder collectionBuilder) {
        mTrack = collectionBuilder.mTrack;
        mScore = collectionBuilder.mScore;
    }

    public Track getTrack() {
        return mTrack;
    }

    public Double getScore() {
        return mScore;
    }

    public static final class CollectionBuilder {
        private Track mTrack;
        private Double mScore;

        public CollectionBuilder() {
        }

        public CollectionBuilder Track(Track track) {
            mTrack = track;
            return this;
        }

        public CollectionBuilder Score(Double score) {
            mScore = score;
            return this;
        }

        public Collection build() {
            return new Collection(this);
        }
    }
}
