package com.framgia.music_24.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import com.framgia.music_24.R;

/**
 * Created by CuD HniM on 18/08/22.
 */
public class SearchViewAnimate {

    private static final float MAX_ALPHA = 1.0f;
    private static final float MIN_ALPHA = 0.0f;
    private static final int DURATION = 250;
    private static final int TWO = 2;
    private static final int ZERO = 0;
    private static Toolbar mToolbar;
    private static Context mContext;

    public static void animateSearchToolbar(final Context context, int numberOfMenuIcon,
            boolean containsOverflow, boolean show, final Toolbar toolbar,
            final DrawerLayout drawerLayout) {

        toolbar.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        drawerLayout.setStatusBarBackgroundColor(
                ContextCompat.getColor(context, R.color.color_gray_600));
        mToolbar = toolbar;
        mContext = context;
        if (show) {
            if (isHigherLolipop()) {
                setupToolbarAnimate(containsOverflow, numberOfMenuIcon, MIN_ALPHA, drawerLayout);
            } else {
                setupTransition();
            }
        } else {
            if (isHigherLolipop()) {
                setupToolbarAnimate(containsOverflow, numberOfMenuIcon, DURATION, drawerLayout);
            } else {
                setupAnimation();
            }
            drawerLayout.setStatusBarBackgroundColor(
                    getThemeColor(context, R.attr.colorPrimaryDark));
        }
    }

    private static void setupToolbarAnimate(boolean containsOverflow, int numberOfMenuIcon,
            float startRadius, final DrawerLayout drawerLayout) {
        boolean checkAnimate = startRadius == MIN_ALPHA;
        Animator createCircularReveal = null;
        int width = mToolbar.getWidth() - (containsOverflow ? mContext.getResources()
                .getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
                : ZERO) - ((mContext.getResources()
                .getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)
                * numberOfMenuIcon) / TWO);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(mContext.getResources()) ? mToolbar.getWidth() - width : width,
                        mToolbar.getHeight() / TWO, checkAnimate ? MIN_ALPHA : (float) width,
                        checkAnimate ? (float) width : MIN_ALPHA);

                if (!checkAnimate) {
                    createCircularReveal.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mToolbar.setBackgroundColor(
                                    getThemeColor(mContext, R.attr.colorPrimary));
                            drawerLayout.setStatusBarBackgroundColor(
                                    getThemeColor(mContext, R.attr.colorPrimaryDark));
                        }
                    });
                }
            }
        }
        createCircularReveal.setDuration(DURATION);
        createCircularReveal.start();
    }

    private static void setupTransition() {
        TranslateAnimation translateAnimation =
                new TranslateAnimation(MIN_ALPHA, MIN_ALPHA, (float) (-mToolbar.getHeight()),
                        MIN_ALPHA);
        translateAnimation.setDuration(DURATION);
        mToolbar.clearAnimation();
        mToolbar.startAnimation(translateAnimation);
    }

    private static void setupAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(MAX_ALPHA, MIN_ALPHA);
        Animation translateAnimation = new TranslateAnimation(MIN_ALPHA, MIN_ALPHA, MIN_ALPHA,
                (float) (-mToolbar.getHeight()));
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(DURATION);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mToolbar.setBackgroundColor(getThemeColor(mContext, R.attr.colorPrimary));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mToolbar.startAnimation(animationSet);
    }

    private static boolean isHigherLolipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray typedArray = theme.obtainStyledAttributes(new int[] { id });
        int result = typedArray.getColor(ZERO, ZERO);
        typedArray.recycle();
        return result;
    }
}
