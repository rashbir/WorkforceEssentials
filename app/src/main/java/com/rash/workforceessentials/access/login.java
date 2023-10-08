package com.rash.workforceessentials.access;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.rash.workforceessentials.R;
import com.rash.workforceessentials.workspace.dashboard;

import java.util.Timer;
import java.util.TimerTask;

public class login extends AppCompatActivity {
    Activity activity = login.this;
    TextInputEditText user_id, password;
    MaterialButton login_button;
    public Timer exitTimer;
    public Boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeViews();
        initializePreLoadings();
        initializeListener();

          }



    private void initializeViews() {
        user_id = findViewById(R.id.user_id);
        password = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
    }

    private void initializePreLoadings() {
//        Intent serviceIntent = new Intent(this, LocationService.class);
//        startService(serviceIntent);
    }

    private void initializeListener() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String access_user_id = String.valueOf(user_id.getText());
                String access_password = String.valueOf(password.getText());

                AndroidNetworking.post("https://tdsinfra.admiralerp.com/gateway/android/workforce_essentials/access.php?page=login_verify")
                        .addBodyParameter("user_id", access_user_id)
                        .addBodyParameter("password", access_password)
                        .setPriority(Priority.IMMEDIATE)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
//                                Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                                if (response.equals(Integer.toString(100))) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("workforce_essentials_access_db", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("login", true);
                                    editor.putString("user_id", access_user_id);
                                    editor.apply();

                                    Intent intent = new Intent(activity, dashboard.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    int errorCode = Integer.parseInt(response);
                                    String description = "";

                                    switch (errorCode) {
                                        case 101:
                                            description = "To access your account, kindly furnish us with either your user ID or registered mobile number.";
                                            break;
                                        case 102:
                                            description = "We kindly request you to provide the password for authentication.";
                                            break;
                                        case 103:
                                            description = "I'm sorry, but the User ID provided does not exist. Please check and try again.";
                                            break;
                                        case 104:
                                            description = "Your User ID is currently inactive. Kindly get in touch with our HR department promptly.";
                                            break;
                                        case 105:
                                            description = "The password you've entered does not match the provided user ID or is invalid. Please double-check your credentials and try again.";
                                            break;
                                        default:
                                            break;
                                    }

                                    Snackbar.make(activity.getWindow().getDecorView().getRootView(), description, Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(activity, "Error: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
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
}