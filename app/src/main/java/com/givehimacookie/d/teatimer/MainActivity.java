package com.givehimacookie.d.teatimer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final static int GREEN_TEA = 3000;
    final static int BLACK_TEA = 5000;
    final static long BREWING_TIME_GREEN = 5000L;
    final static long BREWING_TIME_BLACK = 4000L;
    Button greentea;
    Button blacktea;
    int teaNumberId = 0;
    LinearLayout mTeaSelector;
    LinearLayout mCountDown;
    TextView mTvCountDown;
    Button mStopButton;
    Vibrator vibri;
    long[] pattern = {300, 400};
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greentea = findViewById(R.id.green_tea_button);
        greentea.setText(getResources().getText(R.string.green_tea));
        blacktea = findViewById(R.id.black_tea_button);
        blacktea.setText(getResources().getText(R.string.black_tea));
        mTeaSelector = findViewById(R.id.ll_tea_selector);
        mCountDown = findViewById(R.id.ll_countdown);
        mTvCountDown = findViewById(R.id.tv_counter);
        mStopButton = findViewById(R.id.stop_button);
        //initiate vibrator
        vibri = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        // Set a click listener for greentea button
        greentea.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the greentea button is clicked on.
            @Override
            public void onClick(View view) {
                teaNumberId = GREEN_TEA;
                switchToCountDown(GREEN_TEA);
            }
        });
        blacktea.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the greentea button is clicked on.
            @Override
            public void onClick(View view) {
                teaNumberId = BLACK_TEA;
                switchToCountDown(BLACK_TEA);
            }
        });
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToTeaSelection();
                stopAlarm();
            }
        });
    }


    void switchToCountDown(int teaId) {
        long brewingTime = 0L;

        switch (teaId) {
            case GREEN_TEA:
                setTitle(getResources().getString(R.string.green_tea));
                mCountDown.setBackgroundColor(getResources().getColor(R.color.greentea));
                brewingTime = BREWING_TIME_GREEN;
                Log.e("tea selected:", getString(R.string.green_tea));
                break;
            case BLACK_TEA:
                setTitle(getResources().getString(R.string.black_tea));
                mCountDown.setBackgroundColor(getResources().getColor(R.color.blacktea));
                brewingTime = BREWING_TIME_BLACK;
                Log.e("tea selected:", getString(R.string.black_tea));
                break;
        }

        mTeaSelector.setVisibility(View.INVISIBLE);
        mCountDown.setVisibility(View.VISIBLE);
        mStopButton.setText(R.string.cancel_timer);
        new asyncChrono().execute(brewingTime);
    }

    void switchToTeaSelection() {
        setTitle(getResources().getString(R.string.app_name));
        mTeaSelector.setVisibility(View.VISIBLE);
        mCountDown.setVisibility(View.INVISIBLE);
    }

    void startAlarm() {
        mTeaSelector.setVisibility(View.INVISIBLE);
        mCountDown.setVisibility(View.VISIBLE);
        mStopButton.setText(R.string.cancel_timer);
        vibri.vibrate(pattern, 0);
        Log.e("ALARM", "ALARM");
    }

    void stopAlarm() {

        vibri.cancel();
        Log.e("alarm", "over");
    }

    void updateUI(int secondsleft) {
        mTvCountDown.setText("seconds left: " + secondsleft);
    }

    class asyncChrono extends AsyncTask<Long, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Long... targets) {
            long seconds = 0L;
            while (seconds <= targets[0]) {
                try {
                    publishProgress((int) ((targets[0] - seconds) / 1000));
                    seconds += 1000L;
                    Thread.sleep(1000);
                    Log.e("seconds: ", "" + seconds);

                } catch (InterruptedException e) {
                    e.fillInStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
            startAlarm();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
            updateUI(values[0]);
        }
    }
}

