package com.producttbd.spacemanchuck;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.producttbd.spacemanchuck.throwlistening.AccelerometerListener;
import com.producttbd.spacemanchuck.throwlistening.ThrowCompletedListener;
import com.producttbd.spacemanchuck.throwlistening.ThrowStateTracker;
import com.producttbd.spacemanchuck.ui.AnimatorFactory;

public class ThrowListeningActivity extends AppCompatActivity
                                    implements ThrowCompletedListener {
    private View mImageView;
    private View mInstructionsText;
    private View mThrowCommandText;
    private View mReadyButton;
    private AccelerometerListener mAccelerometerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throw_listening);
        mImageView = findViewById(R.id.imageView);
        mInstructionsText = findViewById(R.id.instructions);
        mThrowCommandText = findViewById(R.id.throwCommand);
        mReadyButton = findViewById(R.id.readyButton);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ThrowStateTracker throwStateTracker =  new ThrowStateTracker(this);
        mAccelerometerListener = new AccelerometerListener(sensorManager, throwStateTracker);
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

    /** For ThrowCompletedListener */
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
        mAccelerometerListener.startListening();
        mInstructionsText.setVisibility(View.INVISIBLE);
        mReadyButton.setVisibility(View.INVISIBLE);
        mThrowCommandText.setVisibility(View.VISIBLE);
    }

    private void setStandbyState() {
        mAccelerometerListener.stopListening();
        mThrowCommandText.setVisibility(View.INVISIBLE);
        mInstructionsText.setVisibility(View.VISIBLE);
        mReadyButton.setVisibility(View.VISIBLE);
    }
}
