package com.example.attendancemanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {
    private Spinner spinnerClasses, spinnerDates;
    private com.google.android.material.button.MaterialButton btnGenerate; // Correct class
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private DBHelper dbHelper;
    private ArrayList<Integer> classIds;
    private ArrayList<String> datesList;
    private PieChart pieChart;
    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Initialize views with CORRECT IDs
        spinnerClasses = findViewById(R.id.spinnerClasses);
        spinnerDates = findViewById(R.id.spinnerDates);
        btnGenerate = findViewById(R.id.btnGenerate); // Fixed ID (btnGenerate instead of btnShowReports)
        recyclerView = findViewById(R.id.recyclerViewReports);
//        pieChart = findViewById(R.id.pieChart);
        tvSummary = findViewById(R.id.tvSummary);

        dbHelper = new DBHelper(this);
        classIds = new ArrayList<>();
        datesList = new ArrayList<>();

        setupSpinners();
        setupRecyclerView();

        // Set click listener for CORRECT BUTTON
        btnGenerate.setOnClickListener(v -> loadAttendance());
    }

    private void setupSpinners() {
        // Load classes
        ArrayList<String> classNames = new ArrayList<>();
        Cursor cursor = dbHelper.getAllClassesRaw();
        while (cursor.moveToNext()) {
            classIds.add(cursor.getInt(0));
            classNames.add(cursor.getString(1));
        }
        cursor.close();

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this,
                R.layout.simple_spinner_item, classNames);
        classAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerClasses.setAdapter(classAdapter);

        // Load dates
        Cursor dateCursor = dbHelper.getUniqueDates();
        while (dateCursor.moveToNext()) {
            datesList.add(dateCursor.getString(0));
        }
        dateCursor.close();

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this,
                R.layout.simple_spinner_item, datesList);
        dateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDates.setAdapter(dateAdapter);
    }

    private void setupRecyclerView() {
        adapter = new ReportAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadAttendance() {
        int classPosition = spinnerClasses.getSelectedItemPosition();
        int datePosition = spinnerDates.getSelectedItemPosition();

        if (classPosition < 0 || datePosition < 0) {
            Toast.makeText(this, "Select a course and date!", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = classIds.get(classPosition);
        String date = datesList.get(datePosition);

        Cursor cursor = dbHelper.getAttendanceByClassAndDate(classId, date);
        List<AttendanceRecord> records = new ArrayList<>();

        while (cursor.moveToNext()) {
            String studentName = cursor.getString(0);
            String status = cursor.getString(1);
            records.add(new AttendanceRecord(studentName, status));
        }
        cursor.close();

        adapter.updateData(records);
        updateChart(records); // Implement chart logic here
    }

    private void updateChart(List<AttendanceRecord> records) {
        // Add pie chart logic
    }
}