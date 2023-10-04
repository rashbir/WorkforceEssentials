package com.rash.workforceessentials.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.rash.workforceessentials.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class attendance_marking extends BottomSheetDialogFragment {
    FloatingActionButton attendance_button;
    CoordinatorLayout coordinatorLayout;
    FusedLocationProviderClient fusedLocationProviderClient;
MaterialTextView currrent_location;

//    String lattitude, longitude, address, city, country;

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
        View view = inflater.inflate(R.layout.fragment_attendance_marking, container, false);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout1);
        currrent_location = view.findViewById(R.id.currrent_location);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        view.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, screenHeight / 2));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String[] addressesArray = new String[addresses.size()];
                    for (int i = 0; i < addresses.size(); i++) {
                        addressesArray[i] = addresses.get(i).getAddressLine(0);
                    }
                    String addressesString = Arrays.toString(addressesArray);
                    String addressesStringWithoutBrackets = addressesString.substring(1, addressesString.length() - 1);
                    currrent_location.setText(addressesStringWithoutBrackets);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        attendance_button = getActivity().findViewById(R.id.attendance_button);
        attendance_button.setEnabled(true);
        super.onDismiss(dialog);
    }
}