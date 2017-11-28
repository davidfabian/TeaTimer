package com.givehimacookie.d.teatimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final static int GREEN_TEA = 3000;
    final static int BLACK_TEA = 5000;
    final static int NOTIFICATION_ID = 101;
    //change this to real timing before release
    final static long BREWING_TIME_GREEN = 4000L;
    //change this to real timing before release
    final static long BREWING_TIME_BLACK = 300000L;
    final static int COUNTDOWN_UPDATE = 1;
    final static int ALARM_NOTIFICATION = 2;
    static asyncChrono timerThread;
    long brewingTime = 0L;
    ImageButton greentea;
    ImageButton blacktea;
    LinearLayout mTeaSelector;
    LinearLayout mCountDown;
    TextView mTvCountDown;
    Button mStopButton;
    Vibrator vibri;
    Ringtone ringring;
    long[] pattern = {300, 400};
    boolean CountdownFinished = false;
    Context context;
    BroadcastReceiver mReceiver;
    NotificationManager mNotifyMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect views and variables
        greentea = findViewById(R.id.green_tea_button);
//        greentea.setText(getResources().getText(R.string.green_tea));
        blacktea = findViewById(R.id.black_tea_button);
//        blacktea.setText(getResources().getText(R.string.black_tea));
        mTeaSelector = findViewById(R.id.ll_tea_selector);
        mCountDown = findViewById(R.id.ll_countdown);
        mTvCountDown = findViewById(R.id.tv_counter);
        mStopButton = findViewById(R.id.stop_button);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("close_app");
        registerReceiver(mReceiver, filter);

        /*
        check if countdown is ongoing, recreate views accordingly.
         */
        if (CountdownFinished) {
            mTeaSelector.setVisibility(View.INVISIBLE);
            mCountDown.setVisibility(View.VISIBLE);
            mStopButton.setText(getTitle() + " " + getResources().getString(R.string.ready));
            mTvCountDown.setText(getTitle() + " " + getResources().getString(R.string.ready));
        } else {
            stopAlarm();
        }

        //initiate vibrator
        vibri = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //get reference to alarm sound
        context = getBaseContext();
        Uri notification = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
        ringring = RingtoneManager.getRingtone(context, notification);

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
        //Stop/reset button to reset to default screen and stop asynctask/alarm/countdown
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    timerThread.cancel(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopAlarm();
            }
        });
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }


    void switchToCountDown(int teaId) {

        switch (teaId) {
            case GREEN_TEA:
                setTitle(getResources().getString(R.string.green_tea));
                mCountDown.setBackgroundColor(getResources().getColor(R.color.greentea));
                brewingTime = BREWING_TIME_GREEN;
                break;
            case BLACK_TEA:
                setTitle(getResources().getString(R.string.black_tea));
                mCountDown.setBackgroundColor(getResources().getColor(R.color.blacktea));
                brewingTime = BREWING_TIME_BLACK;
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
        CountdownFinished = false;
    }

    void stopAlarm() {
        CountdownFinished = true;
        setTitle(getResources().getString(R.string.app_name));
        mTeaSelector.setVisibility(View.VISIBLE);
        mCountDown.setVisibility(View.INVISIBLE);
        try {
            timerThread.cancel(true);
            cancelNotification(context);
            ringring.stop();
            vibri.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startAlarm() {
        CountdownFinished = true;
        createNotification(ALARM_NOTIFICATION, null);
        mTeaSelector.setVisibility(View.INVISIBLE);
        mCountDown.setVisibility(View.VISIBLE);
        mStopButton.setText(getTitle() + " " + getResources().getString(R.string.ready));
        mTvCountDown.setText(getTitle() + " " + getResources().getString(R.string.ready));
        vibri.vibrate(pattern, 0);
        ringring.play();
    }

    String formatTime(int secondsleft) {
        String formattedtime = String.format("%1$02d:%2$02d", secondsleft / 60, secondsleft % 60);

        return formattedtime;
    }

    void updateUI(int secondsleft) {
        mTvCountDown.setText(getTitle() + " " + getResources().getString(R.string.will_be_ready) + " " + formatTime(secondsleft));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        try {
            ringring.stop();
            vibri.cancel();
            cancelNotification(context);
            timerThread.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createNotification(int notificationType, String timeLeft) {
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_teacup);

        Intent bringToFront = new Intent(this, MainActivity.class);
        bringToFront.setAction(Intent.ACTION_MAIN);
        bringToFront.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent resultIntent = new Intent("close_app");

        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(this,
                (int) System.currentTimeMillis(),
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent updatePendingIntent = PendingIntent.getActivities(this,
                (int) System.currentTimeMillis(),
                new Intent[]{bringToFront},
                PendingIntent.FLAG_UPDATE_CURRENT);

        switch (notificationType) {
            case COUNTDOWN_UPDATE:
                mBuilder.setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(timeLeft)
                        .addAction(R.drawable.ic_teacup, getResources().getString(R.string.open_app), updatePendingIntent)
                        .addAction(R.drawable.ic_cancel, getResources().getString(R.string.cancel_timer), resultPendingIntent);
                break;
            case ALARM_NOTIFICATION:
                mBuilder.setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getText(R.string.ready))
                        .addAction(R.drawable.ic_cancel, getResources().getString(R.string.cancel_timer), resultPendingIntent);
                break;
        }
//        mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

    void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(NOTIFICATION_ID);
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
                } catch (InterruptedException e) {
                    CountdownFinished = false;
                    e.fillInStackTrace();
                }
            }
            CountdownFinished = true;
            return CountdownFinished;
        }

        @Override
        protected void onPostExecute(Boolean properFinished) {
            if (properFinished) {
                startAlarm();
            } else {
                stopAlarm();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... secondsLeft) {
            super.onProgressUpdate(secondsLeft);
            updateUI(secondsLeft[0]);
            createNotification(COUNTDOWN_UPDATE, formatTime(secondsLeft[0]));
        }
    }
}

