package com.producttbd.spacemanchuck;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.producttbd.spacemanchuck.tosslistening.TossResult;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple Fragment to display results of the the toss.
 */
public class ResultsFragment extends Fragment {

    private static final String TAG = ResultsFragment.class.getSimpleName();

    private static final Random RAND = new Random();
    private static final double[] LEVELS = {0.25, 0.75, 2.0};
    private static final int[] HEADLINES_LEVEL_0 = {R.string.result_headline_level_0_0,
            R.string.result_headline_level_0_1, R.string.result_headline_level_0_2,
            R.string.result_headline_level_0_3, R.string.result_headline_level_0_4};
    private static final int[] HEADLINES_LEVEL_1 = {R.string.result_headline_level_1_0,
            R.string.result_headline_level_1_1, R.string.result_headline_level_1_2,
            R.string.result_headline_level_1_3, R.string.result_headline_level_1_4};
    private static final int[] HEADLINES_LEVEL_2 = {R.string.result_headline_level_2_0,
            R.string.result_headline_level_2_1, R.string.result_headline_level_2_2,
            R.string.result_headline_level_2_3, R.string.result_headline_level_2_4};
    private static final int[] HEADLINES_LEVEL_3 = {R.string.result_headline_level_3_0,
            R.string.result_headline_level_3_1, R.string.result_headline_level_3_2,
            R.string.result_headline_level_3_3, R.string.result_headline_level_3_4};
    private static final int[] HEADLINES_PERSONAL_BEST = {R.string.result_headline_personal_best_0,
            R.string.result_headline_personal_best_1, R.string.result_headline_personal_best_2,
            R.string.result_headline_personal_best_3};

    private OnFragmentInteractionListener mListener;
    private TextView mReactionHeadlineTextView;
    private TextView mHeightTextView;
    private double mHeight = 0.0;
    private String mResultDebugString = null;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public void setThrowResults(TossResult tossResult) {
        mHeight = tossResult.HeightMeters;
        mResultDebugString = tossResult.DebugString;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        TextView mReactionHeadlineTextView = (TextView) view.findViewById(R.id.reactionHeadline);
        TextView mHeightTextView = (TextView) view.findViewById(R.id.heightText);
        SharedPreferences sharedPreferences =
                getContext().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        float personalBestHeight =
                sharedPreferences.getFloat(getString(R.string.personal_high_score_key), -1.0f);
        boolean firstThrow = personalBestHeight < 0.0f;
        boolean personalBest = (float) mHeight > personalBestHeight;
        if (personalBest) {
            Log.d(TAG, "Logging personal best score: " + mHeight);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(getString(R.string.personal_high_score_key), (float) mHeight);
            editor.apply();
        }

        // Set the reaction headline (e.g. "Good job!")
        mReactionHeadlineTextView.setText(getHeadlineString(firstThrow, personalBest, mHeight));

        // Set the height
        mHeightTextView.setText(getString(R.string.height_text, mHeight));

        // Set the debug output
        if (mResultDebugString != null) {
            Log.d(TAG, mResultDebugString);
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    private String getHeadlineString(boolean firstThrow, boolean personalBest, double height) {
        if (firstThrow) {
            return getString(R.string.result_headline_first_throw);
        } else if (personalBest) {
            return getRandomString(HEADLINES_PERSONAL_BEST);
        } else if (height < LEVELS[0]) {
            return getRandomString(HEADLINES_LEVEL_0);
        } else if (height < LEVELS[1]) {
            return getRandomString(HEADLINES_LEVEL_1);
        } else if (height < LEVELS[2]) {
            return getRandomString(HEADLINES_LEVEL_2);
        } else {
            return getRandomString(HEADLINES_LEVEL_3);
        }
    }

    private String getRandomString(@NonNull int[] headlines) {
        return getString(headlines[RAND.nextInt(headlines.length)]);
    }

    public interface OnFragmentInteractionListener {
        void onRetryRequested();
    }
}
