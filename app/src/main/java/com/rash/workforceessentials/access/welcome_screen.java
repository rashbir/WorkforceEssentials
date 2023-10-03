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
import com.rash.workforceessentials.workspace.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class welcome_screen extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    };
    Activity activity = welcome_screen.this;
    FrameLayout frameLayout;
    MaterialButton login;
    public Timer exitTimer;
    public Boolean doubleBackToExitPressedOnce = false;
    public Boolean checkSigninStatus;

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
                checkNetworkStatus();
            }
        });
    }
    private void initializeViews() {
        frameLayout = findViewById(R.id.framelayout);
        login = findViewById(R.id.login);
    }

    private void checkNetworkStatus() {
        registerNetworkListener();
        if (NetworkUtils.isNetworkAvailable(this)) {
            checkAndRequestPermissions();
        } else {
            showNetworkUnavailableDialog();
        }
    }
    private void registerNetworkListener() {
        NetworkUtils.registerConnectivityChangeListener(this, new NetworkUtils.ConnectivityChangeListener() {
            @Override
            public void onNetworkAvailable() {
                checkAndRequestPermissions();
            }

            @Override
            public void onNetworkUnavailable() {
                runOnUiThread(() -> showNetworkUnavailableDialog());
            }
        });
    }
    private void showNetworkUnavailableDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
//        builder.setIcon(R.drawable.info);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please check the status of your internet connection and attempt the action once more. Ensure you have a stable and active internet connection before proceeding. If the issue persists, please contact our customer support team for further assistance.");
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkNetworkStatus();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finishAffinity();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }
    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
        } else {
            proceedToSignIn();
        }
    }
    private void proceedToSignIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("workforce_essentials_access_db", MODE_PRIVATE);
        checkSigninStatus = sharedPreferences.getBoolean("login", false);

        if(checkSigninStatus) {
            intent = new Intent(activity, dashboard.class);
        } else {
            intent = new Intent(activity, com.rash.workforceessentials.access.login.class);
        }
        startActivity(intent);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, permissions[0], Toast.LENGTH_SHORT).show();
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                proceedToSignIn();
            } else {
                showPermissionExplanationDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showPermissionExplanationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setIcon(R.drawable.info);
        builder.setTitle("Permission Required");
        builder.setMessage("To fully utilize the features and functionalities of this app, we kindly request that you grant the necessary permissions. Your approval will enhance your overall experience and allow us to provide you with a seamless and customized service. Your privacy and data security are of utmost importance to us. Thank you for your cooperation in ensuring a smooth and tailored experience.");
        builder.setPositiveButton("Open Application Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                finishAffinity();
//            }
//        });
//        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                finishAffinity();
//            }
//        });
//        builder.setCancelable(false);
        builder.create().show();
    }
    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        finish();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finishAffinity();
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