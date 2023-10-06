package com.rash.workforceessentials.access;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rash.workforceessentials.R;
import com.rash.workforceessentials.libraries.NetworkUtils;
import com.rash.workforceessentials.libraries.access_permissions;
import com.rash.workforceessentials.workspace.dashboard;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        initializeViews();

        // Start the fade out animation after 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOutFrameLayout();
            }
        }, 4500);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessPermissions = new access_permissions();
                String networkStatus = accessPermissions.checkNetworkStatus(activity);
                if (networkStatus.equals("true")) {
                    proceedToSignIn();
                }
            }
        });
    }

    private void initializeViews() {
        frameLayout = findViewById(R.id.framelayout);
        login = findViewById(R.id.login);
    }

    private void proceedToSignIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("workforce_essentials_access_db", MODE_PRIVATE);
        checkSigninStatus = sharedPreferences.getBoolean("login", false);

        intent = new Intent();
        if (checkSigninStatus) {
            intent.setClass(activity, dashboard.class);
        } else {
            intent.setClass(activity, com.rash.workforceessentials.access.login.class);
        }
        startActivity(intent);
//        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        accessPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, activity);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity(); // Finish the current activity
        } else {
            exitTimer = new Timer();
            exitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

            doubleBackToExitPressedOnce = true;

            Toast.makeText(activity, "Press back again to exit.", Toast.LENGTH_SHORT).show();
        }
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
}