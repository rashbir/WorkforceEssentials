package com.rash.workforceessentials;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rash.workforceessentials.libraries.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class access_permissions extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.USE_BIOMETRIC,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    Activity activity = access_permissions.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_permissions);

        checkNetworkStatus();

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
        Intent intent = new Intent(access_permissions.this, sign_in.class);
        startActivity(intent);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(access_permissions.this);
        ActivityCompat.startActivity(access_permissions.this, intent, options.toBundle());
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
        builder.setMessage("Please grant the required permissions to use this app.");
        builder.setPositiveButton("Open Application Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finishAffinity();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finishAffinity();
            }
        });

        builder.setCancelable(false);
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
//        super.onBackPressed();
        finishAffinity();
    }
}
