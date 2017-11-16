package com.example.d.teatimer;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by d on 11/13/2017.
 */

public class CountDownActivity extends AppCompatActivity {

    final static int GREEN_TEA = 10;
    final static int BLACK_TEA = 20;
    TextView mTextField;
    Long teaTimer;
    String teatype;
    Ringtone ringring;
    Vibrator vibri;
    long[] pattern = {300, 400};
    CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown);
        //get context
        final Context context = getApplicationContext();
        //attach views to variables
        final LinearLayout mRootView = findViewById(R.id.countdown_main);
        final TextView mTextField = findViewById(R.id.tv_counter);
        final Button mStopButton = findViewById(R.id.stop_button);

        //initiate vibri, the vibrator
        vibri = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //get reference to alarm sound
        Uri notification = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
        ringring = RingtoneManager.getRingtone(context, notification);

        //getting the intent extra to decide tea type.
        //setting tea variables (color, brewing time, tea name
        final int teaExtra = getIntent().getExtras().getInt("teaExtra");

        if (teaExtra == GREEN_TEA) {
            teaTimer = 3000L;
            mRootView.setBackgroundResource(R.color.greentea);
            teatype = getResources().getString(R.string.green_tea);
            setTitle(teatype);

        } else if (teaExtra == BLACK_TEA) {
            teaTimer = 5000L;
            mRootView.setBackgroundResource(R.color.blacktea);
            teatype = getResources().getString(R.string.black_tea);
            setTitle(teatype);
        }
        //something went wrong. invalid tea type
        else {
            finish();
        }

        //a new instance of countdowntimer, ticks every second
        timer = new CountDownTimer(teaTimer, 1000) {

            //refreshing remaining time every second,
            public void onTick(long millisUntilFinished) {
                mTextField.setText(teatype + " " + getResources().getString(R.string.will_be_ready) + " " + millisUntilFinished / 1000 + " " + getResources().getString(R.string.seconds));
            }

            //when timer runs out, change button text start alarm and display a toast
            public void onFinish() {
                //set button text to teatype ready
                mStopButton.setText(teatype + " " + getResources().getString(R.string.ready));
                //start alarm and vibration
                startAlarm();
                //display toast
                Toast toast = Toast.makeText(context, teatype + " " + getResources().getString(R.string.ready), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 50);
                toast.show();
                //replace countdown timer with Ready!
                mTextField.setText(R.string.ready);
            }
            //start the timer
        }.start();
        //after timer started, set button text to cancel
        mStopButton.setText(R.string.cancel_timer);
    }

    //executed when alarm starts: starts vibrating and playing alarm sound repeatedly
    public void startAlarm() {

        vibri.vibrate(pattern, 0);
        ringring.play();
        timer.cancel();
    }

    //stop the alarms or cancel countdown, exit activity
    public void stopAlarm(View view) {
        timer.cancel();
        ringring.stop();
        vibri.cancel();
        finish();
    }


}