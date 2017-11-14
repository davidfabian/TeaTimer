package com.example.d.teatimer;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.os.VibrationEffect.createOneShot;

public class MainActivity extends AppCompatActivity {

Button greentea;
Button blacktea;

private TextView mTextField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greentea =  (Button) findViewById(R.id.green_tea_button);
        blacktea =  (Button) findViewById(R.id.black_tea_button);

        // Set a click listener for greentea button
        greentea.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the greentea button is clicked on.
            @Override
            public void onClick(View view) {
                Intent countdownIntent= new Intent(MainActivity.this, CountDownActivity.class);
                countdownIntent.putExtra("teaExtra", 10);
                startActivity(countdownIntent);
            }
        });
        blacktea.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the greentea button is clicked on.
            @Override
            public void onClick(View view) {
                Intent countdownIntent= new Intent(MainActivity.this, CountDownActivity.class);
                countdownIntent.putExtra("teaExtra", 20);
                startActivity(countdownIntent);
            }
        });
    }
}
