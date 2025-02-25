package com.example.attendancemanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddStudentActivity extends AppCompatActivity {
    private Spinner spinnerClasses;
    private EditText etStudentName, etRollNo;
    private Button btnAddStudent;
    private DBHelper dbHelper;
    private ArrayList<Integer> classIds;
    private ArrayList<String> classNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        dbHelper = new DBHelper(this);
        spinnerClasses = findViewById(R.id.spinnerClasses);
        etStudentName = findViewById(R.id.editTextStudentName);
        etRollNo = findViewById(R.id.editTextRollNo);
        btnAddStudent = findViewById(R.id.buttonAddStudent);

        loadClasses();
        btnAddStudent.setOnClickListener(v -> addStudent());
    }

    private void loadClasses() {
        classIds = new ArrayList<>();
        classNames = new ArrayList<>();
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

    private void addStudent() {
        String name = etStudentName.getText().toString().trim();
        String rollNo = etRollNo.getText().toString().trim();
        int position = spinnerClasses.getSelectedItemPosition();

        if (position < 0 || position >= classIds.size()) {
            Toast.makeText(this, "Select a Course first!", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = classIds.get(position);

        if (name.isEmpty() || rollNo.isEmpty()) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
        } else {
            if (dbHelper.addStudent(classId, name, rollNo)) {
                Toast.makeText(this, "Student added!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add student", Toast.LENGTH_SHORT).show();
            }
        }
    }
}