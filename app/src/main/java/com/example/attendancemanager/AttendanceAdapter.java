package com.example.attendancemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private final List<Student> students;
    private final Map<Integer, String> attendanceStatus;

    public AttendanceAdapter(List<Student> students) {
        this.students = students;
        this.attendanceStatus = new HashMap<>();
        // Initialize all students as "Present" by default
        for (Student student : students) {
            attendanceStatus.put(student.getId(), "Present");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.tvStudentName.setText(student.getName());

        // Set initial toggle state
        String status = attendanceStatus.get(student.getId());
        holder.toggleGroup.check(
                "Present".equals(status) ? R.id.togglePresent : R.id.toggleAbsent
        );

        // Handle toggle changes
        holder.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String newStatus = (checkedId == R.id.togglePresent) ? "Present" : "Absent";
                attendanceStatus.put(student.getId(), newStatus);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    // Get attendance status for a student
    public String getAttendanceStatus(int studentId) {
        return attendanceStatus.getOrDefault(studentId, "Present");
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        MaterialButtonToggleGroup toggleGroup;
        MaterialButton togglePresent, toggleAbsent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            toggleGroup = itemView.findViewById(R.id.toggleGroup);
            togglePresent = itemView.findViewById(R.id.togglePresent);
            toggleAbsent = itemView.findViewById(R.id.toggleAbsent);
        }
    }
}