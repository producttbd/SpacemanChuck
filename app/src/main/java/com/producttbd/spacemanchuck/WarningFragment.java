package com.producttbd.spacemanchuck;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.producttbd.spacemanchuck.ui.AnimatorFactory;


/**
 * A Fragment to show the warning before playing.
 * Activities that contain this fragment must implement the
 * {@link WarningFragment.OnWarningFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WarningFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = WarningFragment.class.getSimpleName();

    private OnWarningFragmentInteractionListener mListener;
    private View mImageView;
    private View mWarningText;
    private View mAcceptButton;
    private View mRejectButton;

    public WarningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        mImageView = view.findViewById(R.id.imageView);
        mWarningText = view.findViewById(R.id.warningText);
        mAcceptButton = view.findViewById(R.id.acceptButton);
        mAcceptButton.setOnClickListener(this);
        mRejectButton = view.findViewById(R.id.rejectButton);
        mRejectButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWarningFragmentInteractionListener) {
            mListener = (OnWarningFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(mImageView.getVisibility() != View.VISIBLE){
//            animateOpening();
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");
        switch (view.getId()) {
            case R.id.acceptButton:
                onClickAccept(view);
                break;
            default:
            case R.id.rejectButton:
                onClickReject(view);
                break;
        }
    }

    private void onClickReject(View view) {
        Log.d(TAG, "onClickReject");
        mListener.onWarningReject();
    }

    private void onClickAccept(View view) {
        Log.d(TAG, "onClickAccept");
        animateClose();
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

    private void animateClose() {
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
                mListener.onWarningAccept();
            }
        });
        animSet.start();
    }

    public interface OnWarningFragmentInteractionListener {
        void onWarningAccept();
        void onWarningReject();
    }
}
