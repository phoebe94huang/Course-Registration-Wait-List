package com.example.waitlisthw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "waitlist_db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    // Create tables
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(waitList.CREATE_TABLE);
    }

    @Override
    // Upgrade database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + waitList.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertEntry(String student, String priority, String studentID, String email) {
        // get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // no need to add them
        values.put(waitList.COLUMN_STUDENT, student);
        values.put(waitList.COLUMN_PRIORITY, priority);
        values.put(waitList.COLUMN_STUDENT_ID, studentID);
        values.put(waitList.COLUMN_EMAIL, email);

        // insert row
        long id = db.insert(waitList.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // returns new row id
        return id;
    }

    public waitList getEntry(long id) {
        // get readable database
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(waitList.TABLE_NAME,
                new String[]{waitList.COLUMN_ID, waitList.COLUMN_STUDENT, waitList.COLUMN_PRIORITY, waitList.COLUMN_STUDENT_ID,
                        waitList.COLUMN_EMAIL, waitList.COLUMN_TIMESTAMP},
                waitList.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare student object
        waitList entry = new waitList(
                cursor.getInt(cursor.getColumnIndex(waitList.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(waitList.COLUMN_STUDENT)),
                cursor.getString(cursor.getColumnIndex(waitList.COLUMN_PRIORITY)),
                cursor.getString(cursor.getColumnIndex(waitList.COLUMN_STUDENT_ID)),
                cursor.getString(cursor.getColumnIndex(waitList.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(waitList.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return entry;
    }

    public List<waitList> getAllEntries() {
        List<waitList> entries = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + waitList.TABLE_NAME + " ORDER BY " +
                waitList.COLUMN_PRIORITY + " DESC"; // List ordered by priority level

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // adding to list
        if (cursor.moveToFirst()) {
            do {
                waitList entry = new waitList();
                entry.setId(cursor.getInt(cursor.getColumnIndex(waitList.COLUMN_ID)));
                entry.setStudent(cursor.getString(cursor.getColumnIndex(waitList.COLUMN_STUDENT)));
                entry.setPriority(cursor.getString(cursor.getColumnIndex(waitList.COLUMN_PRIORITY)));
                entry.setStudentID(cursor.getString(cursor.getColumnIndex(waitList.COLUMN_STUDENT_ID)));
                entry.setEmail(cursor.getString(cursor.getColumnIndex(waitList.COLUMN_EMAIL)));
                entry.setTimestamp(cursor.getString(cursor.getColumnIndex(waitList.COLUMN_TIMESTAMP)));

                entries.add(entry);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return wait list
        return entries;
    }

    public int getWaitListCount() {
        String countQuery = "SELECT  * FROM " + waitList.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateEntry(waitList entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(waitList.COLUMN_STUDENT, entry.getStudent());
        values.put(waitList.COLUMN_PRIORITY, entry.getPriority());
        values.put(waitList.COLUMN_STUDENT_ID, entry.getStudentID());
        values.put(waitList.COLUMN_EMAIL, entry.getEmail());

        // updating row
        return db.update(waitList.TABLE_NAME, values, entry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
    }

    public void deleteEntry(waitList entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(waitList.TABLE_NAME, waitList.COLUMN_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
        db.close();
    }
}
