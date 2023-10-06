package com.rash.workforceessentials.libraries;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rash.workforceessentials.R;

import java.util.ArrayList;
import java.util.List;

public class access_permissions {
    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private AlertDialog networkUnavailableDialog;
    String returnStatement = "false";
    String permissionsStatus = "false";

    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    public String checkNetworkStatus(Activity activity) {
        registerNetworkListener(activity);
        if (NetworkUtils.isNetworkAvailable(activity)) {
            String permissionsStatus = checkAndRequestPermissions(activity);
            if (permissionsStatus.equals("true")) {
                return "true";
            }
        } else {
            showNetworkUnavailableDialog(activity);
        }
        return permissionsStatus;
    }

    public void registerNetworkListener(Activity activity) {
        NetworkUtils.registerConnectivityChangeListener(activity, new NetworkUtils.ConnectivityChangeListener() {
            @Override
            public void onNetworkAvailable() {
                if (networkUnavailableDialog != null && networkUnavailableDialog.isShowing()) {
                    networkUnavailableDialog.dismiss();
                }
                checkAndRequestPermissions(activity);
            }
            @Override
            public void onNetworkUnavailable() {
                activity.runOnUiThread(() -> showNetworkUnavailableDialog(activity));
            }
        });
    }

    private String checkAndRequestPermissions(Activity activity) {
        List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
        } else {
            returnStatement = "true";
        }
        return returnStatement;
    }

    private void showNetworkUnavailableDialog(Activity activity) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity);
        dialogBuilder
                .setIcon(R.drawable.info)
                .setTitle("No Internet Connection")
                .setMessage("Please check the status of your internet connection and attempt the action once more. Ensure you have a stable and active internet connection before proceeding. If the issue persists, please contact our customer support team for further assistance.")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkNetworkStatus(activity);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        activity.finishAffinity();
                    }
                })
                .setCancelable(false);

        networkUnavailableDialog = dialogBuilder.create();
        networkUnavailableDialog.show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                showPermissionExplanationDialog(activity);
            }
        } else {
            activity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionExplanationDialog(Activity activity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setIcon(R.drawable.info);
        builder.setTitle("Permission Required");
        builder.setMessage("To fully utilize the features and functionalities of this app, we kindly request that you grant the necessary permissions. Your approval will enhance your overall experience and allow us to provide you with a seamless and customized service. Your privacy and data security are of utmost importance to us. Thank you for your cooperation in ensuring a smooth and tailored experience.");
        builder.setPositiveButton("Open Application Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings(activity);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finishAffinity();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.finishAffinity();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void openAppSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);

        activity.finish();

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);

        activity.finishAffinity();
    }
}
