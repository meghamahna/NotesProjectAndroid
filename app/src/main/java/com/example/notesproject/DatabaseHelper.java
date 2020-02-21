package com.example.notesproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "Notes" ;
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "MyNotes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LNG = "longitude";




    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER NOT NULL CONSTRAINT PK_notes PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " varchar(200) NOT NULL, " +
                COLUMN_DESCRIPTION + " varchar(200) NOT NULL, " +
                COLUMN_CATEGORY + " varchar(200) NOT NULL, " +
                COLUMN_DATE + " varchar(200) NOT NULL, " +
                COLUMN_LAT + " double NOT NULL, " +
                COLUMN_LNG + " double NOT NULL);";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean addNotes(String title, String description, String category, String date, double lat, double lng){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_LAT, lat);
        cv.put(COLUMN_LNG, lng);

        return sqLiteDatabase.insert(TABLE_NAME, null, cv) != -1;
    }

    public Cursor getAllNotes(String category){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * from " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + "=?", new String[]{category});
    }


    public boolean updateNotes(int id, String title, String description, String category){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, category);


        return sqLiteDatabase.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0 ;


    }




    public boolean deleteNotes(String ColumnName, String Value){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.delete(TABLE_NAME, ColumnName + "=?", new String[]{Value}) > 0 ;

    }

    boolean deleteNote(int id){
        SQLiteDatabase sqLiteDatabase  = getWritableDatabase();

        //the delete method returns the  number of rows effected
        return sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID +"=?", new String[]{String.valueOf(id)}) > 0;
    }
}
