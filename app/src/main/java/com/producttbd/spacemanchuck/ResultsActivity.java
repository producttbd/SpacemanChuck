package com.producttbd.spacemanchuck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Random;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = ResultsActivity.class.getSimpleName();
    public static final String THROW_RESULT_DEBUG = "com.producttbd.spacemanchuck.THROW_RESULT_DEBUG";
    public static final String THROW_RESULT_HEIGHT = "com.producttbd.spacemanchuck.THROW_RESULT_HEIGHT";

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

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        double height = intent.getDoubleExtra(THROW_RESULT_HEIGHT, 0.0);

        mSharedPreferences =
                getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        float personalBestHeight = mSharedPreferences.getFloat(getString(R.string.personal_high_score_key), -1.0f);
        boolean firstThrow = personalBestHeight < 0.0f;
        boolean personalBest = (float) height > personalBestHeight;
        if (personalBest) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putFloat(getString(R.string.personal_high_score_key), (float) height);
            editor.commit();
        }

        // Set the reaction headline (e.g. "Good job!")
        TextView reactionHeadlineTextView = (TextView) findViewById(R.id.reactionHeadline);
        reactionHeadlineTextView.setText(getHeadlineString(firstThrow, personalBest, height));

        // Set the height
        TextView heightTextView = (TextView) findViewById(R.id.heightText);
        heightTextView.setText(getString(R.string.height_text, height));

        // Set the debug output
        String debugString = intent.getStringExtra(THROW_RESULT_DEBUG);
        if (debugString != null) {
            Log.d(TAG, debugString);
        }
    }

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

    private String getRandomString(int[] headlines) {
        return getString(headlines[RAND.nextInt(headlines.length)]);
    }
}
