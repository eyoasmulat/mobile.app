package com.example.attendancemanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {
    MaterialButton buttonManageClasses, buttonTakeAttendance, buttonReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize buttons with CORRECT IDs
        buttonManageClasses = findViewById(R.id.buttonManageClasses);
        buttonTakeAttendance = findViewById(R.id.buttonTakeAttendance); // Fixed ID
        buttonReports = findViewById(R.id.buttonReports);

        // Manage Classes Button Click
        buttonManageClasses.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ManageClassesActivity.class));
        });

        // Take Attendance Button Click
        buttonTakeAttendance.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, TakeAttendanceActivity.class));
        });

        // View Reports Button Click
        buttonReports.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ReportsActivity.class));
        });
    }
}