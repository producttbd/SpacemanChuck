package com.producttbd.spacemanchuck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ThrowListeningActivity.THROW_RESULTS);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.resultsText);
        textView.setText(message);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

}
