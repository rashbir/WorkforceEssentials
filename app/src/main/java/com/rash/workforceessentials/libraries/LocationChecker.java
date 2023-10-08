package com.rash.workforceessentials.libraries;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rash.workforceessentials.R;

public class LocationChecker {
    private final Activity activity;
    private BroadcastReceiver locationReceiver;

    public LocationChecker(Activity activity) {
        this.activity = activity;
    }

    public void checkLocationSettingsAndRegisterReceiver() {
        checkLocationSettings();
    }

    public void checkLocationSettings() {
        if (locationReceiver != null) {
            activity.unregisterReceiver(locationReceiver);
        }
        registerLocationReceiver();

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
//                    showLocationUnavailableDialog(activity, e);
                   /* try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult() in your activity.
                        e.startResolutionForResult(activity, 1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }*/

                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity);
                    dialogBuilder
                            .setIcon(R.drawable.info)
                            .setTitle("Turn on location services")
                            .setMessage("Please enable the location services on your device to allow our app to provide you with a seamless and personalized experience. By granting location access, you'll unlock a range of features that enhance your app usage. Rest assured that your privacy is important to us, and we only use your location data to improve your experience within the app.")
                            .setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        e.startResolutionForResult(activity, 1);
                                    } catch (IntentSender.SendIntentException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
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
//            Location is not enabled, show a dialog to enable it.
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            activity.startActivity(intent);
//
//            unregisterLocationReceiver();

            if (locationReceiver != null) {
                activity.unregisterReceiver(locationReceiver);
                registerLocationReceiver();
            }

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
                    } catch (ResolvableApiException e) {
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity);
                        dialogBuilder
                                .setIcon(R.drawable.info)
                                .setTitle("Turn on location services")
                                .setMessage("Please enable the location services on your device to allow our app to provide you with a seamless and personalized experience. By granting location access, you'll unlock a range of features that enhance your app usage. Rest assured that your privacy is important to us, and we only use your location data to improve your experience within the app.")
                                .setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            e.startResolutionForResult(activity, 1);
                                        } catch (IntentSender.SendIntentException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    } catch (ApiException e) {
                        // Handle other errors.
                        Toast.makeText(activity, "Error: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void registerLocationReceiver() {
        locationReceiver = new LocationReceiver(this);
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        activity.registerReceiver(locationReceiver, intentFilter);
    }

    public void unregisterLocationReceiver() {
        if (locationReceiver != null) {
            activity.unregisterReceiver(locationReceiver);
            locationReceiver = null;
        }
    }

    private static class LocationReceiver extends BroadcastReceiver {
        private final LocationChecker locationChecker;

        public LocationReceiver(LocationChecker locationChecker) {
            this.locationChecker = locationChecker;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
                locationChecker.checkLocationEnabled();
            }
        }
    }
}