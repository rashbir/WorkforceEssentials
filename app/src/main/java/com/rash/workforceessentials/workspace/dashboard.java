package com.rash.workforceessentials.workspace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.rash.workforceessentials.R;

import java.util.Timer;
import java.util.TimerTask;

public class dashboard extends AppCompatActivity {

    public Timer exitTimer;
    public Boolean doubleBackToExitPressedOnce = false;

    Activity activity = dashboard.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            finishAffinity(); // Finish the current activity
        } else {
            exitTimer = new Timer();
            exitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            },2000);

            doubleBackToExitPressedOnce = true;

            Toast.makeText(activity,"Press back again to exit.", Toast.LENGTH_SHORT).show();
        }
    }
}