package com.rash.workforceessentials.workspace;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.rash.workforceessentials.R;
import com.rash.workforceessentials.fragments.attendance_marking;
import com.rash.workforceessentials.libraries.BottomAppBarCutCornersTopEdge;

public class dashboard extends AppCompatActivity {
    private BottomAppBar bottomAppBar;
    Activity activity = dashboard.this;
    FloatingActionButton attendance_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        preLoadings();
        buttonListeners();

        attendance_marking attendanceMarking = new attendance_marking();

//        // Set the height of the bottom sheet dialog fragment
//        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) attendanceMarking.getView());
//        behavior.setPeekHeight(100);

        attendanceMarking.show(getSupportFragmentManager(),"Attendance Marking");
        attendance_button.setEnabled(false);
    }

    private void preLoadings() {
        BottomAppBarCutCornersTopEdge bottomAppBarCutCornersTopEdge = new BottomAppBarCutCornersTopEdge(bottomAppBar.getFabCradleMargin(), bottomAppBar.getFabCradleRoundedCornerRadius(), bottomAppBar.getCradleVerticalOffset());

        MaterialShapeDrawable background = (MaterialShapeDrawable) bottomAppBar.getBackground();
        background.setShapeAppearanceModel(background.getShapeAppearanceModel().toBuilder().setTopEdge(bottomAppBarCutCornersTopEdge).build());
    }

    private void initializeViews() {
        bottomAppBar = findViewById(R.id.bottomAppBar);
        attendance_button = findViewById(R.id.attendance_button);
    }

    private void buttonListeners() {
        attendance_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendance_marking attendanceMarking = new attendance_marking();//
                attendanceMarking.show(getSupportFragmentManager(),"Attendance Marking");
                attendance_button.setEnabled(false);
            }
        });
    }


}