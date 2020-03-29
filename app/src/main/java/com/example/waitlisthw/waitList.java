package com.example.waitlisthw;

public class waitList {
    public static final String TABLE_NAME = "waitlist";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STUDENT = "student";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_STUDENT_ID = "studentID";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String student;
    private String priority;
    private String studentID;
    private String email;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // id of note
                    + COLUMN_STUDENT + " TEXT," // student name
                    + COLUMN_PRIORITY + " TEXT," // priority level
                    + COLUMN_STUDENT_ID + " TEXT," // student ID number
                    + COLUMN_EMAIL + " TEXT," // email
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" // date note was entered
                    + ")";

    public waitList() {
    }

    public waitList(int id, String student, String priority, String studentID, String email, String timestamp) {
        this.id = id;
        this.student = student;
        this.priority = priority;
        this.studentID = studentID;
        this.email = email;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStudentID(){
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}