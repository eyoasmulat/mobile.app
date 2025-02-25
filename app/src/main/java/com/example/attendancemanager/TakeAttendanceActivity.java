package com.example.attendancemanager;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TakeAttendanceActivity extends AppCompatActivity {
    private Spinner spinnerClasses;
    private Button btnDate, btnSave;
    private TextView tvDate;
    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private DBHelper dbHelper;
    private Calendar calendar;
    private List<Student> studentList;
    private String selectedDate;
    private ArrayList<Integer> classIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        dbHelper = new DBHelper(this);
        calendar = Calendar.getInstance();
        studentList = new ArrayList<>();
        classIds = new ArrayList<>();

        spinnerClasses = findViewById(R.id.spinnerClasses);
        btnDate = findViewById(R.id.btnDate);
        tvDate = findViewById(R.id.tvDate);
        btnSave = findViewById(R.id.btnSave);
        recyclerView = findViewById(R.id.recyclerViewAttendance);

        setupSpinner();
        setupDatePicker();
        setupRecyclerView();

        btnSave.setOnClickListener(v -> saveAttendance());
    }

    private void setupSpinner() {
        ArrayList<String> classNames = new ArrayList<>();
        Cursor cursor = dbHelper.getAllClassesRaw();

        while (cursor.moveToNext()) {
            classIds.add(cursor.getInt(0)); // class_id
            classNames.add(cursor.getString(1)); // class_name
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.simple_spinner_item, classNames);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerClasses.setAdapter(adapter);
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateLabel();
                loadStudents();
            }
        };

        btnDate.setOnClickListener(v -> new DatePickerDialog(
                TakeAttendanceActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());
    }

    private void updateDateLabel() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        selectedDate = sdf.format(calendar.getTime());
        tvDate.setText("Date: " + selectedDate);
    }

    private void setupRecyclerView() {
        adapter = new AttendanceAdapter(studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadStudents() {
        int position = spinnerClasses.getSelectedItemPosition();
        if (position < 0 || position >= classIds.size()) {
            Toast.makeText(this, "Select a course first!", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = classIds.get(position);
        Cursor cursor = dbHelper.getStudentsByClass(classId);
        studentList.clear();

        while (cursor.moveToNext()) {
            int studentId = cursor.getInt(0);
            String name = cursor.getString(2);
            String rollNo = cursor.getString(3);
            studentList.add(new Student(studentId, name, rollNo));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void saveAttendance() {
        if (selectedDate == null) {
            Toast.makeText(this, "Select a date first!", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = spinnerClasses.getSelectedItemPosition();
        if (position < 0 || position >= classIds.size()) {
            Toast.makeText(this, "Select a course first!", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = classIds.get(position);

        for (Student student : studentList) {
            String status = adapter.getAttendanceStatus(student.getId());
            if (dbHelper.checkAttendanceExists(student.getId(), selectedDate)) {
                dbHelper.updateAttendance(student.getId(), selectedDate, status);
            } else {
                dbHelper.addAttendance(student.getId(), classId, selectedDate, status);
            }
        }
        Toast.makeText(this, "Attendance saved!", Toast.LENGTH_SHORT).show();
    }
}