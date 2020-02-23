package com.line.imagenote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.line.imagenote.models.Attachment;
import com.line.imagenote.models.Note;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 노트를 저장하기 위한 SQLiteOpenHelper 클래스이다.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "noteDB";

    // 노트 테이블 칼럼명
    private static final String TABLE_NOTES = "notes_list";
    private static final String COLUMN_NOTE_KEY = "time_created";
    private static final String COLUMN_TIME_CREATED = "time_created";
    private static final String COLUMN_TIME_MODIFIED = "time_modified";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";

    // 첨부파일 테이블 칼럼명 (현재는 이미지타입만 구현)
    private static final String TABLE_ATTACHMENT = "attachment_list";
    private static final String COLUMN_ATTACHMENT_ID = "attachment_id";
    private static final String COLUMN_NOTE_ID = "note_id";
    private static final String COLUMN_URI = "uri";
    private static final String COLUMN_TYPE = "type";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES = "CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_TIME_CREATED + " INTEGER PRIMARY KEY,"
                + COLUMN_TIME_MODIFIED + " INTEGER,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT" + ")";

        String CREATE_FILES = "CREATE TABLE " + TABLE_ATTACHMENT + "("
                + COLUMN_ATTACHMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTE_ID + " INTEGER,"
                + COLUMN_URI + " TEXT,"
                + COLUMN_TYPE + " TEXT" + ")";

        db.execSQL(CREATE_NOTES);
        db.execSQL(CREATE_FILES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }


    /**
     * 노트를 최초로 추가하거나 변경사항을 업데이트한다.
     * @param note
     */
    public void insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();

        cValues.put(COLUMN_TIME_CREATED, note.getTimeCreated());
        cValues.put(COLUMN_TIME_MODIFIED, note.getTimeModified());
        cValues.put(COLUMN_TITLE, note.getTitle());
        cValues.put(COLUMN_CONTENT, note.getContent());

        db.insertWithOnConflict(TABLE_NOTES, COLUMN_NOTE_KEY, cValues, SQLiteDatabase.CONFLICT_REPLACE);

    }


    /**
     * 노트 테이블에 있는 모든 노트들을 가져온다.
     * @return ArrayList<Note>
     */
    public ArrayList<Note> getAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Note> notesList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NOTES + " ORDER BY " + COLUMN_TIME_MODIFIED + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            long timeCreated = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_CREATED));
            long timeModified = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_MODIFIED));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));

            Note noteItem = new Note(timeCreated, timeModified, title, content);
            notesList.add(noteItem);

        }
        cursor.close();
        return notesList;
    }

    /**
     * 선택한 noteId에 해당하는 노트 정보들을 가져온다.
     * @param noteId
     * @return Note
     */
    public Note getNoteById(long noteId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES,
                null,
                COLUMN_NOTE_KEY + "=?", new String[]{String.valueOf(noteId)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        long timeCreated = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_CREATED));
        long timeModified = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_MODIFIED));
        String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
        String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));

        Note noteItem = new Note(timeCreated, timeModified, title, content);

        cursor.close();
        return noteItem;
    }


    /**
     * 선택한 noteId에 해당하는 note를 삭제한다.
     * 이 때, 해당 노트안에 있는 첨부파일(이미지)도 삭제된다.
     * @param noteId
     */
    public void deleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_NOTE_KEY + " = ?", new String[]{String.valueOf(noteId)});
        db.delete(TABLE_ATTACHMENT, COLUMN_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});

        db.close();
    }

    /**
     * 노트 안에 첨부파일을 추가한다.
     * @param noteId
     * @param uri 파일경로
     * @param type 이미지 파일인 경우 type=image
     */
    public void insertAttachment(long noteId, String uri, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();

        cValues.put(COLUMN_NOTE_ID, noteId);
        cValues.put(COLUMN_URI, uri);
        cValues.put(COLUMN_TYPE, type);

        db.insertWithOnConflict(TABLE_ATTACHMENT, COLUMN_ATTACHMENT_ID, cValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * 노트 안의 모든 첨부파일(이미지)들을 가져온다.
     * @param noteId
     * @param type
     * @return ArrayList<Attachment>
     */
    public ArrayList<Attachment> getAttachments(long noteId, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Attachment> arrayList = new ArrayList<>();

        String whereClause = COLUMN_NOTE_ID + " = ? AND " + COLUMN_TYPE + " = ?";
        String[] whereArgs = new String[] {String.valueOf(noteId), type};

        Cursor cursor = db.query(TABLE_ATTACHMENT,
                null,
                whereClause,
                whereArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ATTACHMENT_ID));
                String uri = cursor.getString(cursor.getColumnIndex(COLUMN_URI));

                Attachment attachment = new Attachment(id, uri);
                arrayList.add(attachment);
            }
        }

        cursor.close();
        return arrayList;
    }

    /**
     * 노트에 이미지가 있는 경우, 첫번째 이미지 주소를 반환한다.
     * @param noteId
     * @return 이미지 주소
     */
    public String getThumbnail(Long noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String uri = "";

        Cursor cursor = db.query(TABLE_ATTACHMENT,
                null,
                COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(noteId)},
                null, null, COLUMN_ATTACHMENT_ID + " ASC", String.valueOf(1));


        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            return uri;
        } else {
            uri = cursor.getString(cursor.getColumnIndex(COLUMN_URI));
        }

        Log.d(TAG, "getThumbnail: " + uri);

        cursor.close();
        return uri;
    }

    /**
     * 첨부파일(이미지)을 삭제한다.
     * @param fileId
     */
    public void deleteAttachment(int fileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATTACHMENT, COLUMN_ATTACHMENT_ID + " = ?", new String[]{String.valueOf(fileId)});
        db.close();
    }


}
