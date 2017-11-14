package com.example.d.teatimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by d on 11/13/2017.
 */

public class CountDownActivity extends AppCompatActivity {

    TextView mTextField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown);
        mTextField = findViewById(R.id.tv_counter);
        final Context context = getApplicationContext();
        Bundle extras = getIntent().getExtras();
        mTextField.setBackgroundResource(extras.getInt("teaColor"));
        final Long teatimer = extras.getLong("timerMs");
        final String teatype = extras.getString("teaType");

        setTitle(teatype);

        final Vibrator vibri = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        new CountDownTimer(teatimer, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                //TODO: create button that pops up when alarm starts
                //TODO: create repeating alert until a button is pressed

                long[] pattern = {300, 400,300, 400,300, 400,300, 400,300, 400};
                vibri.vibrate(pattern, -1);

                Toast.makeText(context ,teatype + " ready!", Toast.LENGTH_LONG).show();
                mTextField.setText("done!");
            }
        }.start();


    }
}