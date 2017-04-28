package com.producttbd.spacemanchuck;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.producttbd.spacemanchuck.throwlistening.AccelerometerListener;
import com.producttbd.spacemanchuck.throwlistening.ThrowCompletedListener;
import com.producttbd.spacemanchuck.throwlistening.ThrowStateTracker;
import com.producttbd.spacemanchuck.ui.AnimatorFactory;


/**
 * A Fragment to show a UI for start and stop accelerometer listening.
 */
public class TossListeningFragment extends Fragment
        implements ThrowCompletedListener, View.OnClickListener {
    private View mImageView;
    private View mInstructionsText;
    private View mThrowCommandText;
    private View mReadyButton;
    private AccelerometerListener mAccelerometerListener;
    private OnTossListeningFragmentInteractionListener mListener;

    public TossListeningFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SensorManager sensorManager =
                (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        ThrowStateTracker throwStateTracker =  new ThrowStateTracker(this);
        mAccelerometerListener = new AccelerometerListener(sensorManager, throwStateTracker);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_toss_listening, container, false);
        mImageView = view.findViewById(R.id.imageView);
        mInstructionsText = view.findViewById(R.id.instructions);
        mThrowCommandText = view.findViewById(R.id.throwCommand);
        mReadyButton = view.findViewById(R.id.readyButton);
        mReadyButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTossListeningFragmentInteractionListener) {
            mListener = (OnTossListeningFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setStandbyState();
        mListener = null;
    }

    /**
     * For ThrowCompletedListener
     */
    @Override
    public void onThrowCompleted(double height, String debugString) {
        setStandbyState();
        mListener.onThrowCompleted(height, debugString);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.readyButton) {
            setReadyToThrowState();
        }
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

    public interface OnTossListeningFragmentInteractionListener {
        void onThrowCompleted(double height, String debugString);
    }
}