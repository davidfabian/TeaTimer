package com.example.d.teatimer;

import android.content.Context;
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
    Long teaTimer;
    String teatype;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown);
        mTextField = findViewById(R.id.tv_counter);
        final Context context = getApplicationContext();
//getting the extra to decide tea type.
        final int teaExtra = getIntent().getExtras().getInt("teaExtra");

        if (teaExtra == 10) {
            teaTimer = 180000L;
            mTextField.setBackgroundResource(R.color.greentea);
            teatype = "Green Tea";
            setTitle(teatype);

        } else {
            teaTimer = 300000L;
            mTextField.setBackgroundResource(R.color.blacktea);
            teatype = "Black Tea";
            setTitle(teatype);
        }


        final Vibrator vibri = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        new CountDownTimer(teaTimer, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("The " + teatype + " will be ready in " + millisUntilFinished / 1000 + " seconds!");
            }

            public void onFinish() {
                long[] pattern = {300, 400, 300, 400, 300, 400, 300, 400, 300, 400};
                vibri.vibrate(pattern, -1);

                Toast.makeText(context, teatype + " ready!", Toast.LENGTH_LONG).show();
                mTextField.setText("done!");
            }
        }.start();


    }
}