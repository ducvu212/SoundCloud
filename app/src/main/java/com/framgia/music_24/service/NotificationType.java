package com.framgia.music_24.service;

import android.support.annotation.IntDef;

import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_CLEAR;
import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_NEXT;
import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_PAUSE;
import static com.framgia.music_24.service.NotificationType.REQUEST_CODE_PREVIOUS;

/**
 * Created by MyPC on 30/01/2018.
 */

@IntDef({
        REQUEST_CODE_NEXT, REQUEST_CODE_PAUSE, REQUEST_CODE_PREVIOUS, REQUEST_CODE_CLEAR
})
public @interface NotificationType {
    int REQUEST_CODE_NEXT = 0;
    int REQUEST_CODE_PAUSE = 1;
    int REQUEST_CODE_PREVIOUS = 2;
    int REQUEST_CODE_CLEAR = 3;
}
