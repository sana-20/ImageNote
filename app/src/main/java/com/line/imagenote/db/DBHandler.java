package com.line.imagenote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.line.imagenote.models.NoteItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 자동 업로드 된 사진을 저장하기 위한 SQLite 클래스
 * 참고: https://www.tutlane.com/tutorial/android/android-sqlite-database-with-examples
 */
public class DBHandler extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHandler";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "noteDB";

    private static final String TABLE_NOTES = "notes_list";
    private static final String COLUMN_ID = "note_id"; // 자동으로 증가하는 ID
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_PHOTOS = "photos";
    private static final String COLUMN_TIME = "time";


    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES = "CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_PHOTOS + " TEXT,"
                + COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_NOTES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }


    public void insertNote(String title, String content, String photos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();

        // 컬럼의 이름을 key로 하고, 데이터를 넣는다.
        cValues.put(COLUMN_TITLE, title);
        cValues.put(COLUMN_CONTENT, content);
        cValues.put(COLUMN_PHOTOS, photos);

        db.insert(TABLE_NOTES, null, cValues);
        db.close();
        Log.d(TAG, "insertNote: " + showNotes());
    }

    public void updateNote(int id, String newTitle, String newContent, String photos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();

        cValues.put(COLUMN_TITLE, newTitle);
        cValues.put(COLUMN_CONTENT, newContent);
        cValues.put(COLUMN_PHOTOS, photos);

        db.update(TABLE_NOTES, cValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        Log.d(TAG, "updateNote: " + showNotes());
    }


    public ArrayList<NoteItem> getAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<NoteItem> notesList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_TIME + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
            String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
            String photo = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTOS));

            ArrayList photoList = stringToArray(photo);

            NoteItem noteItem = new NoteItem(id, title, content, time, photoList);
            notesList.add(noteItem);

        }
        cursor.close();
        Log.d(TAG, "getAllNotes: " + showNotes());
        return notesList;
    }


    public NoteItem getNoteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES,
                null,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
        String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
        String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
        String photo = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTOS));

        ArrayList photoList = stringToArray(photo);

        NoteItem noteItem = new NoteItem(id, title, content, time, photoList);

        return noteItem;
    }



    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deletePhoto(int id, String photos) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "UPDATE "+TABLE_NOTES +" SET " + COLUMN_PHOTOS+ " = '"+photos+"' WHERE "+COLUMN_ID+ " = "+id;
        db.execSQL(sql);


        Log.d(TAG, "deletePhoto: " + showNotes());
    }


    private ArrayList<String> stringToArray(String photo) {
        Log.d(TAG, "stringToArray: " + photo);
        ArrayList<String> photoList = new ArrayList<>();

        if (!photo.equals("[]")) {
            String string = photo.substring(1, photo.length() - 1);

            String[] arrSplit = string.split(", ");

            for (int i = 0; i < arrSplit.length; i++) {
                photoList.add(arrSplit[i]);
                Log.d(TAG, "stringToArray: " + arrSplit[i]);
            }
        }

        Log.d(TAG, "stringToArray: " + photoList);
        return photoList;
    }


    /**
     //     * 로그 찍기 테스트 코드
     //     */

    public ArrayList<HashMap<String, String>> showNotes() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<HashMap<String, String>> noteList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NOTES;

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            hashMap.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            hashMap.put(COLUMN_CONTENT, cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
            hashMap.put(COLUMN_PHOTOS, cursor.getString(cursor.getColumnIndex(COLUMN_PHOTOS)));
            hashMap.put(COLUMN_TIME, cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
            noteList.add(hashMap);
        }
        cursor.close();

        return noteList;
    }



}
