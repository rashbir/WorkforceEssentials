package com.rash.workforceessentials.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.rash.workforceessentials.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class attendance_marking extends BottomSheetDialogFragment {
    FloatingActionButton attendance_button;
    CoordinatorLayout coordinatorLayout;
    FusedLocationProviderClient fusedLocationProviderClient;
    MaterialTextView currrent_location, latitude, longitude, required_latitude, required_longitude, location_distance;
    Double required_latitude_value, required_longitude_value;
    View view;
    Geocoder geocoder;

    public attendance_marking() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_attendance_marking, container, false);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout1);
        currrent_location = view.findViewById(R.id.currrent_location);

        required_latitude = view.findViewById(R.id.required_latitude);
        required_longitude = view.findViewById(R.id.required_longitude);

        location_distance = view.findViewById(R.id.location_distance);

        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        attendance_button = getActivity().findViewById(R.id.attendance_button);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        view.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, screenHeight / 2));

        geocoder = new Geocoder(view.getContext(), Locale.getDefault());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Toast.makeText(view.getContext(), "Hello", Toast.LENGTH_SHORT).show();
                try {
                    List<Address> userLocationAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    String[] addressesArray = new String[userLocationAddresses.size()];
                    for (int i = 0; i < userLocationAddresses.size(); i++) {
                        addressesArray[i] = userLocationAddresses.get(i).getAddressLine(0);
                    }
                    String addressesString = Arrays.toString(addressesArray);
                    String addressesStringWithoutBrackets = addressesString.substring(1, addressesString.length() - 1);
                    currrent_location.setText(addressesStringWithoutBrackets);
                    latitude.setText(Double.toString(userLocationAddresses.get(0).getLatitude()));
                    longitude.setText(Double.toString(userLocationAddresses.get(0).getLongitude()));

                    required_latitude_value = Double.parseDouble(required_latitude.getText().toString());
                    required_longitude_value = Double.parseDouble(required_longitude.getText().toString());

                    double userLatitude = userLocationAddresses.get(0).getLatitude();
                    double userLongitude = userLocationAddresses.get(0).getLongitude();

                    double specifiedLatitude = required_latitude_value;
                    double specifiedLongitude = required_longitude_value;

                    // Calculate distance using Haversine formula
                    double distance = calculateDistance(userLatitude, userLongitude, specifiedLatitude, specifiedLongitude);

                    location_distance.setText(Double.toString(distance) + " meters");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        attendance_button.setEnabled(true);
        super.onDismiss(dialog);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in meters
        final double R = 6371000; // Earth's radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in meters
    }
}