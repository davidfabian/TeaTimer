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
    //change this to real timing before release
    final static long BREWING_TIME_GREEN = 5000L;
    //change this to real timing before release
    final static long BREWING_TIME_BLACK = 4000L;
    static asyncChrono timerThread;
    long brewingTime = 0L;
    Button greentea;
    Button blacktea;
    LinearLayout mTeaSelector;
    LinearLayout mCountDown;
    TextView mTvCountDown;
    Button mStopButton;
    Vibrator vibri;
    long[] pattern = {300, 400};
    boolean alarmIsOn;
    boolean CountdownFinished = false;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect views and variables
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

        // The code in this method will be executed when the greentea button is clicked on.
        greentea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switchToCountDown(GREEN_TEA);
            }
        });

        // The code in this method will be executed when the blacktea button is clicked on.
        blacktea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switchToCountDown(BLACK_TEA);
            }
        });
        //button to reset to default screen and stop asynctask/alarm/countdown
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerThread.cancel(true);
                resetToTeaSelection();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CountdownFinished) {
            mTeaSelector.setVisibility(View.INVISIBLE);
            mCountDown.setVisibility(View.VISIBLE);
            mStopButton.setText(getTitle() + " " + getResources().getString(R.string.ready));
            mTvCountDown.setText(getTitle() + " " + getResources().getString(R.string.ready));
        } else {
            resetToTeaSelection();
        }
    }

    void switchToCountDown(int teaId) {

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
        startCounter();


    }

    void startCounter() {
        timerThread = new asyncChrono();
        timerThread.execute(brewingTime);
    }

    void resetToTeaSelection() {
        setTitle(getResources().getString(R.string.app_name));
        mTeaSelector.setVisibility(View.VISIBLE);
        mCountDown.setVisibility(View.INVISIBLE);
        try {
            timerThread.cancel(true);
            vibri.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        alarmIsOn = false;
        Log.e("alarm", "over");
    }

    void startAlarm() {
        alarmIsOn = true;
        mTeaSelector.setVisibility(View.INVISIBLE);
        mCountDown.setVisibility(View.VISIBLE);
        mStopButton.setText(getTitle() + " " + getResources().getString(R.string.ready));
        mTvCountDown.setText(getTitle() + " " + getResources().getString(R.string.ready));
        vibri.vibrate(pattern, 0);
        Log.e("ALARM", "ALARM");
    }


    void updateUI(int secondsleft) {
        mTvCountDown.setText(getTitle() + " " + getResources().getString(R.string.will_be_ready) + " " + secondsleft + " " + getResources().getString(R.string.seconds));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerThread.cancel(true);
    }

    class asyncChrono extends AsyncTask<Long, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Long... targets) {
            long seconds = 0L;

            while (seconds <= targets[0] && !isCancelled()) {
                try {
                    CountdownFinished = false;
                    publishProgress((int) ((targets[0] - seconds) / 1000));
                    seconds += 1000L;
                    Thread.sleep(1000L);
                    Log.e("seconds: ", "" + seconds + CountdownFinished);

                } catch (InterruptedException e) {
                    CountdownFinished = false;
                    e.fillInStackTrace();
                    Log.e("interruptexeption", "e= " + e);
                }
            }
            CountdownFinished = true;
            return CountdownFinished;
        }

        @Override
        protected void onPostExecute(Boolean properFinished) {
            Log.e("proper finished? ", " " + properFinished);
            if (properFinished) {
                startAlarm();
            } else {
                Log.e("cancelled", "button pressed");
                resetToTeaSelection();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            updateUI(values[0]);
        }

    }
}

