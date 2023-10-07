package com.rash.workforceessentials.libraries;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.rash.workforceessentials.access.LocationChecker;

public class LocationReceiver extends BroadcastReceiver {
    private LocationChecker locationChecker;

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