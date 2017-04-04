package com.producttbd.spacemanchuck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class TitleActivity extends AppCompatActivity {

    private static final String TAG = TitleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        Log.d(TAG, "onCreate");
    }

    public void onClickAccept(View view) {
        Log.d(TAG, "onClickAccept");
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
}
