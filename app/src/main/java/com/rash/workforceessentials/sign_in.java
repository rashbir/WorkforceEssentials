package com.rash.workforceessentials;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class sign_in extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(sign_in.this, MainActivity.class);
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(sign_in.this);
//                startActivity(intent, options.toBundle());

                // Create an Intent to start the sign_in activity
                Intent intent = new Intent(sign_in.this, MainActivity.class);

                // Define the transition animation options
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        sign_in.this,
                        R.anim.slide_in_from_right, // Enter animation
                        R.anim.slide_out_to_left   // Exit animation
                );

                // Start the activity with the specified animation
                startActivity(intent, options.toBundle());

                // Optionally, you can finish the current activity to prevent going back to it
                finish();
            }
        });
    }
}