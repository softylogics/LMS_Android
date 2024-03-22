package com.dusre.lms.Util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dusre.lms.model.DownloadedVideo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DownloadedVideosDB";

    // Table name
    private static final String TABLE_DOWNLOADED_VIDEOS = "downloaded_videos";

    // Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_COURSE_ID = "course_id";
    private static final String KEY_COURSE_TITLE = "course_title";
    private static final String KEY_SECTION_ID = "section_id";
    private static final String KEY_SECTION_TITLE = "section_title";
    private static final String KEY_VIDEO_FILE_PATH = "video_file_path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table query
        String CREATE_TABLE = "CREATE TABLE " + TABLE_DOWNLOADED_VIDEOS + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DURATION + " TEXT,"
                + KEY_COURSE_ID + " TEXT,"
                + KEY_COURSE_TITLE + " TEXT,"
                + KEY_SECTION_ID + " TEXT,"
                + KEY_SECTION_TITLE + " TEXT,"
                + KEY_VIDEO_FILE_PATH + " TEXT"
                + ")";
        // Create table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADED_VIDEOS);

        // Create tables again
        onCreate(db);
    }

    // Add a downloaded video record
    public long addDownloadedVideo(DownloadedVideo downloadedVideo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID , downloadedVideo.getId());
        values.put(KEY_TITLE, downloadedVideo.getTitle());
        values.put(KEY_DURATION, downloadedVideo.getDuration());
        values.put(KEY_COURSE_ID, downloadedVideo.getCourse_id());
        values.put(KEY_COURSE_TITLE, downloadedVideo.getCourse_title());
        values.put(KEY_SECTION_ID, downloadedVideo.getSection_id());
        values.put(KEY_SECTION_TITLE, downloadedVideo.getSection_title());
        values.put(KEY_VIDEO_FILE_PATH, downloadedVideo.getVideo_file_path());

        // Insert row
        long id = db.insert(TABLE_DOWNLOADED_VIDEOS, null, values);

        // Close database connection
        db.close();

        return id;
    }

    // Get all downloaded videos

    public List<DownloadedVideo> getAllDownloadedVideos() {
        List<DownloadedVideo> downloadedVideosList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DOWNLOADED_VIDEOS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DownloadedVideo downloadedVideo = new DownloadedVideo();
                downloadedVideo.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                downloadedVideo.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                downloadedVideo.setDuration(cursor.getString(cursor.getColumnIndex(KEY_DURATION)));
                downloadedVideo.setCourse_id(cursor.getString(cursor.getColumnIndex(KEY_COURSE_ID)));
                downloadedVideo.setCourse_title(cursor.getString(cursor.getColumnIndex(KEY_COURSE_TITLE)));
                downloadedVideo.setSection_id(cursor.getString(cursor.getColumnIndex(KEY_SECTION_ID)));
                downloadedVideo.setSection_title(cursor.getString(cursor.getColumnIndex(KEY_SECTION_TITLE)));
                downloadedVideo.setVideo_file_path(cursor.getString(cursor.getColumnIndex(KEY_VIDEO_FILE_PATH)));

                // Adding downloaded video to list
                downloadedVideosList.add(downloadedVideo);
            } while (cursor.moveToNext());
        }

        // Close cursor and database connection
        cursor.close();
        db.close();

        return downloadedVideosList;
    }
    // Delete a downloaded video
    // Delete a downloaded video record
    public void deleteDownloadedVideo(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOWNLOADED_VIDEOS, KEY_ID + " = ?",
                new String[]{id});
        db.close();
    }
}
