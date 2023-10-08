package com.rash.workforceessentials.access;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rash.workforceessentials.R;
import com.rash.workforceessentials.libraries.LocationChecker;
import com.rash.workforceessentials.libraries.access_permissions;

import java.util.Timer;
import java.util.TimerTask;

public class welcome_screen extends AppCompatActivity {

    Activity activity = welcome_screen.this;
    FrameLayout frameLayout;
    MaterialButton login;
    public Timer exitTimer;
    public Boolean doubleBackToExitPressedOnce = false, checkSigninStatus;
    access_permissions accessPermissions;
    public Intent intent;

    private LocationChecker locationChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        initializeViews();
        initializePreLoadings();
        initializeListener();
    }

    private void initializeViews() {
        frameLayout = findViewById(R.id.framelayout);
        login = findViewById(R.id.login);
    }

    private void initializePreLoadings() {
        // Start the fade out animation after 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOutFrameLayout();
            }
        }, 4500);
    }

    private void initializeListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessPermissions = new access_permissions();
                String networkStatus = accessPermissions.checkNetworkStatus(activity);

                if (networkStatus.equals("true")) {
                    locationChecker = new LocationChecker(activity);
                    locationChecker.checkLocationSettingsAndRegisterReceiver();

                    proceedToSignIn();
                }
            }
        });
    }

    private void proceedToSignIn() {
//        Toast.makeText(activity, "SUCCESSFUL LOGIN!", Toast.LENGTH_SHORT).show();
//        SharedPreferences sharedPreferences = getSharedPreferences("workforce_essentials_access_db", MODE_PRIVATE);
//        checkSigninStatus = sharedPreferences.getBoolean("login", false);
//
//        intent = new Intent();
//        if (checkSigninStatus) {
//            intent.setClass(activity, dashboard.class);
//        } else {
//            intent.setClass(activity, com.rash.workforceessentials.access.login.class);
//        }
//        startActivity(intent);
//        finish();
    }

    private void fadeOutFrameLayout() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                frameLayout.setAlpha(alpha);
            }
        });
        animator.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        accessPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, activity);
    }

    // Handle the result of the location settings resolution in onActivityResult.
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                // Location settings have been updated successfully.
//            } else {
//                // The user canceled or didn't update location settings.
//                Toast.makeText(this, "Location services not enabled.", Toast.LENGTH_SHORT).show();
//                locationChecker.checkLocationSettingsAndRegisterReceiver();
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
        } else {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (locationChecker != null) {
            locationChecker.registerLocationReceiver();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}