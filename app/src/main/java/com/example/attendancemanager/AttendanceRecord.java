package com.example.attendancemanager;

public class AttendanceRecord {
    private String studentName;
    private String status;

    public AttendanceRecord(String studentName, String status) {
        this.studentName = studentName;
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStatus() {
        return status;
    }
}