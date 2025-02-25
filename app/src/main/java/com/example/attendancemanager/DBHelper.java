package com.example.attendancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "AttendanceDB";
    private static final int DB_VERSION = 1;

    // Tables and Columns
    public static final String TABLE_USERS = "Users";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    public static final String TABLE_CLASSES = "Classes";
    public static final String COL_CLASS_ID = "class_id";
    public static final String COL_CLASS_NAME = "class_name";

    public static final String TABLE_STUDENTS = "Students";
    public static final String COL_STUDENT_ID = "student_id";
    public static final String COL_STUDENT_NAME = "name";
    public static final String COL_ROLL_NO = "roll_no";

    public static final String TABLE_ATTENDANCE = "Attendance";
    public static final String COL_ATTENDANCE_ID = "attendance_id";
    public static final String COL_DATE = "date";
    public static final String COL_STATUS = "status";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USERNAME + " TEXT PRIMARY KEY,"
                + COL_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_CLASSES + " ("
                + COL_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CLASS_NAME + " TEXT UNIQUE)");

        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " ("
                + COL_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CLASS_ID + " INTEGER,"
                + COL_STUDENT_NAME + " TEXT,"
                + COL_ROLL_NO + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + " ("
                + COL_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_STUDENT_ID + " INTEGER,"
                + COL_CLASS_ID + " INTEGER,"
                + COL_DATE + " TEXT,"
                + COL_STATUS + " TEXT)");

        // Add default admin user
        db.execSQL("INSERT INTO " + TABLE_USERS + " VALUES ('admin', 'admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(db);
    }

    // Class methods
    public boolean addClass(String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLASS_NAME, className);
        long result = db.insert(TABLE_CLASSES, null, values);
        return result != -1;
    }

    public ArrayList<String> getAllClasses() {
        ArrayList<String> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);
        while (cursor.moveToNext()) {
            classes.add(cursor.getString(1));
        }
        cursor.close();
        return classes;
    }

    public Cursor getAllClassesRaw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);
    }

    public int getClassIdByName(String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_CLASS_ID + " FROM " + TABLE_CLASSES +
                " WHERE " + COL_CLASS_NAME + "=?", new String[]{className});

        int classId = -1;
        if (cursor.moveToFirst()) {
            classId = cursor.getInt(0);
        }
        cursor.close();
        return classId;
    }

    public boolean deleteClass(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_STUDENTS, COL_CLASS_ID + "=?", new String[]{String.valueOf(classId)});
            db.delete(TABLE_ATTENDANCE, COL_CLASS_ID + "=?", new String[]{String.valueOf(classId)});
            int deletedRows = db.delete(TABLE_CLASSES, COL_CLASS_ID + "=?", new String[]{String.valueOf(classId)});

            if (deletedRows > 0) {
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }

    // Student methods
    public boolean addStudent(int classId, String name, String rollNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLASS_ID, classId);
        values.put(COL_STUDENT_NAME, name);
        values.put(COL_ROLL_NO, rollNo);
        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1;
    }

    public Cursor getStudentsByClass(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENTS +
                " WHERE " + COL_CLASS_ID + "=?", new String[]{String.valueOf(classId)});
    }

    // Attendance methods
    public boolean addAttendance(int studentId, int classId, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_CLASS_ID, classId);
        values.put(COL_DATE, date);
        values.put(COL_STATUS, status);
        long result = db.insert(TABLE_ATTENDANCE, null, values);
        return result != -1;
    }

    public boolean updateAttendance(int studentId, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        return db.update(TABLE_ATTENDANCE, values,
                COL_STUDENT_ID + "=? AND " + COL_DATE + "=?",
                new String[]{String.valueOf(studentId), date}) > 0;
    }

    public boolean checkAttendanceExists(int studentId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE +
                        " WHERE " + COL_STUDENT_ID + "=? AND " + COL_DATE + "=?",
                new String[]{String.valueOf(studentId), date});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Report methods
    public Cursor getUniqueDates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_ATTENDANCE, null);
    }

    public Cursor getAttendanceByClassAndDate(int classId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT s." + COL_STUDENT_NAME + ", a." + COL_STATUS +
                        " FROM " + TABLE_ATTENDANCE + " a" +
                        " INNER JOIN " + TABLE_STUDENTS + " s ON a." + COL_STUDENT_ID + " = s." + COL_STUDENT_ID +
                        " WHERE a." + COL_CLASS_ID + "=? AND a." + COL_DATE + "=?",
                new String[]{String.valueOf(classId), date}
        );
    }

    // User authentication
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }
}
