package com.rash.workforceessentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class access_permissions extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_permissions);

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            // Request permissions for the needed permissions
            ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSIONS_REQUEST_CODE
            );
        } else {
            // All permissions are granted, proceed to the welcome_screen activity
            startActivity(new Intent(this, sign_in.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions are granted, proceed to the welcome_screen activity
                startActivity(new Intent(this, sign_in.class));
            } else {
                // At least one permission was denied, inform the user and request permissions again
                showPermissionExplanationDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setCancelable(false)
                .setMessage("Please provide the necessary permissions in order to use this app.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request permissions again
                        checkAndRequestPermissions();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the app or handle as needed
                        finish();
                    }
                })
                .setNeutralButton("Neutral", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the app or handle as needed
                        finish();
                    }
                })

                .show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}