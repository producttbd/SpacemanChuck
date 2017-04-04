package com.producttbd.spacemanchuck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = ResultsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        double height = intent.getDoubleExtra(ThrowListeningActivity.THROW_RESULT_HEIGHT, 0.0);

        // Set the reaction headline (e.g. "Good job!")
        TextView reactionHeadlineTextView = (TextView) findViewById(R.id.reactionHeadline);
        reactionHeadlineTextView.setText(getHeadlineString(height));

        // Set the height
        TextView heightTextView = (TextView) findViewById(R.id.heightText);
        heightTextView.setText(getString(R.string.height_text, height));

        // Set the debug output
        String debugString = intent.getStringExtra(ThrowListeningActivity.THROW_RESULT_DEBUG);
        if (debugString != null) {
            Log.d(TAG, debugString);
        }
    }

    private String getHeadlineString(double height) {
        if (height < 0.25) {
            return getString(R.string.result_level_0);
        } else if (height < 0.75) {
            return getString(R.string.result_level_1);
        } else if (height < 2.0) {
            return getString(R.string.result_level_2);
        } else {
            return getString(R.string.result_level_3);
        }
    }
}
