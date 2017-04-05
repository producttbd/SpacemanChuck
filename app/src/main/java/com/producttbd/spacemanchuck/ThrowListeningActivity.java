package com.producttbd.spacemanchuck;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ThrowListeningActivity extends AppCompatActivity implements ThrowListener.ThrowListenerCallback {

    private View mImageView;
    private View mInstructionsText;
    private View mThrowCommandText;
    private View mReadyButton;
    private SensorManager mSensorManager;
    private ThrowListener mThrowListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throw_listening);
        mImageView = findViewById(R.id.imageView);
        mInstructionsText = findViewById(R.id.instructions);
        mThrowCommandText = findViewById(R.id.throwCommand);
        mReadyButton = findViewById(R.id.readyButton);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mThrowListener  = new ThrowListener(mSensorManager, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStandbyState();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && mImageView.getVisibility() != View.VISIBLE) {
            animateOpening();
        }
    }

    /** For ThrowListenerCallback */
    @Override
    public void onThrowCompleted(double height, String debugString) {
        setStandbyState();
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(ResultsActivity.THROW_RESULT_DEBUG, debugString);
        intent.putExtra(ResultsActivity.THROW_RESULT_HEIGHT, height);
        startActivity(intent);
    }

    public void onReadyClick(View view) {
        setReadyToThrowState();
    }

    private void animateOpening() {
        Animator imageAnim = AnimatorFactory.createRevealAnimator(mImageView);
        Animator textAnim = AnimatorFactory.createFlyUpInAnimator(mInstructionsText);
        Animator readyButtonAnim = AnimatorFactory.createFlyUpInAnimator(mReadyButton);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(imageAnim, textAnim, readyButtonAnim);
        animSet.start();
    }

    private void setReadyToThrowState() {
        mThrowListener.startListening();
        mInstructionsText.setVisibility(View.INVISIBLE);
        mReadyButton.setVisibility(View.INVISIBLE);
        mThrowCommandText.setVisibility(View.VISIBLE);
    }

    private void setStandbyState() {
        mThrowCommandText.setVisibility(View.INVISIBLE);
        mInstructionsText.setVisibility(View.VISIBLE);
        mReadyButton.setVisibility(View.VISIBLE);
        mThrowListener.stopListening();
    }
}
