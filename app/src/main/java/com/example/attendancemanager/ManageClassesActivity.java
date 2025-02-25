package com.example.attendancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class ManageClassesActivity extends AppCompatActivity
        implements ClassAdapter.OnClassDeleteListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddClass;
    private MaterialButton btnAddStudents;
    private DBHelper dbHelper;
    private ClassAdapter adapter;
    private ArrayList<String> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_classes);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewClasses);
        fabAddClass = findViewById(R.id.fabAddClass);
        btnAddStudents = findViewById(R.id.btnAddStudents);
        dbHelper = new DBHelper(this);

        // Setup RecyclerView
        classList = new ArrayList<>();
        adapter = new ClassAdapter(classList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadClasses();

        // Add Class FAB Click
        fabAddClass.setOnClickListener(v -> showAddClassDialog());

        // Add Students Button Click
        btnAddStudents.setOnClickListener(v -> {
            if (classList.isEmpty()) {
                Toast.makeText(this, "Add a course first!", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, AddStudentActivity.class));
            }
        });
    }

    private void loadClasses() {
        classList.clear();
        classList.addAll(dbHelper.getAllClasses());
        adapter.notifyDataSetChanged();
    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_class, null);
        EditText etClassName = dialogView.findViewById(R.id.editTextClassName);

        builder.setView(dialogView)
                .setTitle("Add New Course")
                .setPositiveButton("Add", (dialog, which) -> {
                    String className = etClassName.getText().toString().trim();
                    if (!className.isEmpty()) {
                        if (dbHelper.addClass(className)) {
                            loadClasses();
                            Toast.makeText(this, "Course added!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to add Course", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public void onClassDelete(int position) {
        // Get the actual class name
        String className = classList.get(position);

        // Get class ID from database
        int classId = dbHelper.getClassIdByName(className);

        if (classId != -1) { // Ensure valid ID
            if (dbHelper.deleteClass(classId)) {
                classList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete Course", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: Course not found", Toast.LENGTH_SHORT).show();
        }
    }
}
