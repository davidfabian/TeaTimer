package com.example.d.teatimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

Button greentea;
Button blacktea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greentea = findViewById(R.id.green_tea_button);
        blacktea = findViewById(R.id.black_tea_button);

        // Set a click listener for greentea button
        greentea.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the greentea button is clicked on.
            @Override
            public void onClick(View view) {
                Intent countdownIntent= new Intent(MainActivity.this, CountDownActivity.class);
                countdownIntent.putExtra("teaExtra", 10);
                startActivity(countdownIntent);
                finish();
            }
        });
        blacktea.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the greentea button is clicked on.
            @Override
            public void onClick(View view) {
                Intent countdownIntent= new Intent(MainActivity.this, CountDownActivity.class);
                countdownIntent.putExtra("teaExtra", 20);
                startActivity(countdownIntent);
                finish();
            }
        });
    }
}
