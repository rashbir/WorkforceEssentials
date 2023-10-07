package com.rash.workforceessentials.access;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.rash.workforceessentials.libraries.LocationReceiver;

import java.util.List;

public class LocationChecker {
    private final Activity activity;
    private BroadcastReceiver locationReceiver;

    public LocationChecker(Activity activity) {
        this.activity = activity;
    }

    public void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // Location settings are satisfied, you can proceed with location-related operations.
                } catch (ResolvableApiException e) {
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        e.startResolutionForResult(activity, 1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                } catch (ApiException e) {
                    // Handle other errors.
                    Toast.makeText(activity, "Error: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location is not enabled, show a dialog to enable it.
            Toast.makeText(activity, "Location is not enabled, show a dialog to enable it.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);

//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//
//// Check if the intent is resolvable
//            if (intent.resolveActivity(activity.getPackageManager()) != null) {
//
//// Check if the setting is already open
//                ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//                List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
//                if (runningTasks.isEmpty() || runningTasks.get(0).topActivity.getClassName() != "com.android.settings.location.Loc  ationSettings") {
//                    // Start the setting
//                    activity.startActivity(intent);
//                }
//            }
        }

    }

    public void registerLocationReceiver() {
        Toast.makeText(activity, "Check 123456", Toast.LENGTH_SHORT).show();
        locationReceiver = new LocationReceiver(this);
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);

//        if (locationReceiver != null) {
//            unregisterLocationReceiver();
//        }
        activity.registerReceiver(locationReceiver, intentFilter);
    }

    public void unregisterLocationReceiver() {
        if (locationReceiver != null) {
            activity.unregisterReceiver(locationReceiver);
            locationReceiver = null;
        }
    }

//    private static class LocationReceiver extends BroadcastReceiver {
//        private final LocationChecker locationChecker;
//
//        public LocationReceiver(LocationChecker locationChecker) {
//            this.locationChecker = locationChecker;
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent != null && LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
//                locationChecker.checkLocationEnabled();
//            }
//        }
//    }
}
