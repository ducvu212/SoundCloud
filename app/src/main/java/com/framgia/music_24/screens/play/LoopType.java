package com.framgia.music_24.screens.play;

import android.support.annotation.IntDef;

import static com.framgia.music_24.screens.play.LoopType.LOOP_ALL;
import static com.framgia.music_24.screens.play.LoopType.LOOP_ONE;
import static com.framgia.music_24.screens.play.LoopType.NO_LOOP;

/**
 * Created by CuD HniM on 18/09/02.
 */

@IntDef({
        NO_LOOP, LOOP_ONE, LOOP_ALL
})

public @interface LoopType {
    int NO_LOOP = 0;
    int LOOP_ONE = 1;
    int LOOP_ALL = 2;
}
