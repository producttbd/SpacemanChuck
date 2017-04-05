package com.producttbd.spacemanchuck;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.producttbd.spacemanchuck.ui.AnimatorFactory;

public class TitleActivity extends AppCompatActivity {

    private static final String TAG = TitleActivity.class.getSimpleName();

    private View mImageView;
    private View mWarningText;
    private View mAcceptButton;
    private View mRejectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        Log.d(TAG, "onCreate");

        mImageView = findViewById(R.id.imageView);
        mWarningText = findViewById(R.id.warningText);
        mAcceptButton = findViewById(R.id.acceptButton);
        mRejectButton = findViewById(R.id.rejectButton);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus && mImageView.getVisibility() != View.VISIBLE){
            animateOpening();
        }
    }

    public void onClickAccept(View view) {
        Log.d(TAG, "onClickAccept");
        animateTransitionToNextActivity();
    }

    private void startThrowListeningActivity() {
        Intent intent = new Intent(this, ThrowListeningActivity.class);
        startActivity(intent);
    }

    public void onClickExit(View view) {
        Log.d(TAG, "onClickExit");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void animateOpening() {
        Animator imageAnim = AnimatorFactory.createRevealAnimator(mImageView);
        Animator textAnim = AnimatorFactory.createFlyUpInAnimator(mWarningText);
        Animator rejectButtonAnim = AnimatorFactory.createFlyUpInAnimator(mRejectButton);
        Animator acceptButtonAnim = AnimatorFactory.createFlyUpInAnimator(mAcceptButton);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(imageAnim, textAnim, rejectButtonAnim, acceptButtonAnim);
        animSet.start();
    }

    private void animateTransitionToNextActivity() {
        Animator imageAnim = AnimatorFactory.createDisappearAnimator(mImageView);
        Animator textAnim = AnimatorFactory.createFlyDownOutAnimator(mWarningText);
        Animator rejectButtonAnim = AnimatorFactory.createFlyDownOutAnimator(mRejectButton);
        Animator acceptButtonAnim = AnimatorFactory.createFlyDownOutAnimator(mAcceptButton);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(acceptButtonAnim, rejectButtonAnim, textAnim, imageAnim);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startThrowListeningActivity();
            }
        });
        animSet.start();
    }
}
