package com.producttbd.spacemanchuck.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Class to create Animators for the UI.
 */

public class AnimatorFactory {
    private static final float ALPHA_INVISIBLE = 0.0f;
    private static final float ALPHA_VISIBLE = 1.0f;

    private static final float FLY_IN_DISTANCE = 100;

    private static final long LONG_DURATION = 1500;
    private static final long MEDIUM_DURATION = 1000;
    private static final long SHORT_DURATION = 500;
    private static final long VERY_SHORT_DURATION = 250;

    public static Animator createRevealAnimator(View view) {
        view.setVisibility(View.VISIBLE);
        Animator anim;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int cx = view.getWidth() / 2;
            int cy = view.getHeight() / 2;
            float finalRadius = (float) Math.hypot(cx, cy);
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        } else {
            anim = createFadeIn(view);
        }
        anim.setDuration(LONG_DURATION);
        return anim;
    }

    public static Animator createDisappearAnimator(final View view) {
        Animator anim;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int cx = view.getWidth() / 2;
            int cy = view.getHeight() / 2;
            float initialRadius = (float) Math.hypot(cx, cy);
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            anim = createFadeOut(view);
        }
        anim.setDuration(SHORT_DURATION);
        return anim;
    }

    public static Animator createFlyUpInAnimator(View view) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationY", FLY_IN_DISTANCE, 0), createFadeIn(view));
        animSet.setDuration(MEDIUM_DURATION);
        return animSet;
    }

    public static Animator createFlyDownOutAnimator(View view) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationY", FLY_IN_DISTANCE), createFadeOut(view));
        animSet.setDuration(VERY_SHORT_DURATION);
        return animSet;
    }

    private static Animator createFadeIn(View view) {
        view.setAlpha(ALPHA_INVISIBLE);
        return ObjectAnimator.ofFloat(view, "alpha", ALPHA_INVISIBLE, ALPHA_VISIBLE);
    }

    private static Animator createFadeOut(View view) {
        view.setAlpha(ALPHA_VISIBLE);
        return ObjectAnimator.ofFloat(view, "alpha", ALPHA_VISIBLE, ALPHA_INVISIBLE);
    }
}
