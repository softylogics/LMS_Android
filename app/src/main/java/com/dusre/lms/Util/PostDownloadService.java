package com.dusre.lms.Util;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.dusre.lms.model.CourseForPostDownloadService;
import com.dusre.lms.model.DownloadedVideo;
import com.dusre.lms.model.Lesson;
import com.dusre.lms.model.Section;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostDownloadService extends Service {
    Lesson requiredLesson = null;
    private String sectionTitle;
    private String courseTitle;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UserPreferences.init(this);
        Log.d("post Download" , " in service");
        long downloadID = intent.getLongExtra("downloadId", -1);

        // Handle post-download tasks (e.g., file processing, database update)
        // You can retrieve the file using the downloadId and perform necessary operations
        Log.d("download", "in if receiver");
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {

            int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(statusIndex);
            Log.d("download" , status+"");
            Log.d("download" , statusIndex+"");

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                // Download completed successfully
                Log.d("download", "in if if receiver");
                String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                cursor.close();
                callCourse(filePath , this);

            } else if (status == DownloadManager.STATUS_FAILED) {
                // Download failed
                // Handle download failure

                Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show();
            } else if (status == DownloadManager.STATUS_PAUSED) {
                // Download paused
                // Handle download pause
                Toast.makeText(this, "Download Paused", Toast.LENGTH_SHORT).show();
            }
            // Retrieve the file path

            // Store the file path for later access (e.g., in SharedPreferences)
            // Here, for simplicity, we'll just use a class-level variable

        }
        return START_NOT_STICKY;
    }

    private void callCourse(String filePath, PostDownloadService postDownloadService) {
//todo: develop retry policy
        APIClient myVolleyApiClient = new APIClient(this);

            APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    // Handle successful response
                    CourseForPostDownloadService course = parseJsonToCourseList(response);
                    for(Section section : course.getSections()){
                        for(Lesson lesson : section.getLessons()){
                            if(lesson.getId().equals(UserPreferences.getString(Constants.lesson_id_for_post_download_service))){
                                requiredLesson = lesson;
                                sectionTitle = section.getTitle();
                                courseTitle = course.getTitle();
                            }
                        }
                    }

                    String videoId = UUID.randomUUID().toString();
                    DownloadedVideo downloadedVideo = new DownloadedVideo();
                    downloadedVideo = new DownloadedVideo();
                    downloadedVideo.setId(videoId);
                    downloadedVideo.setTitle(requiredLesson.getTitle());
                    downloadedVideo.setDuration(requiredLesson.getDuration());
                    downloadedVideo.setCourse_id(requiredLesson.getCourse_id());
                    downloadedVideo.setCourse_title(courseTitle);
                    downloadedVideo.setSection_id(requiredLesson.getSection_id());
                    downloadedVideo.setSection_title(sectionTitle);
                    downloadedVideo.setUpdateOnServer("0");

                    downloadedVideo.setVideo_file_path(filePath);
                    downloadedVideo.setUpdateOnServer("0");

                    DatabaseHelper dbHelper = new DatabaseHelper(postDownloadService);
                    dbHelper.addDownloadedVideo(downloadedVideo);
                    // Handle successful download

                    updateLessonOnServer(UserPreferences.getint(Constants.lesson_id_for_post_download_service));
                    Log.d("API Response", response);


                }

                @Override
                public void onFailure(VolleyError error) {
                    // Handle failure
//                if(isFragmentAttached) {

                    Log.d("API Response", error.toString());

//                }
                }
            };


            Map<String, String> params = new HashMap<>();
            params.put("auth_token", UserPreferences.getString(Constants.TOKEN));
            params.put("course_id", UserPreferences.getString(Constants.course_id_for_post_download_service));

            myVolleyApiClient.fetchDataFromApi(Constants.url+"course_details_by_id", params, listener , Constants.MY_COURSE_FRAGMENT);

    }
    public CourseForPostDownloadService parseJsonToCourseList(String jsonString) {
        Type listType = new TypeToken<CourseForPostDownloadService>(){}.getType();

        Gson gson = new Gson();
        return gson.fromJson(jsonString, listType);
    }
    private void updateLessonOnServer(int lesson_id) {

        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                //sectionsAdapter.reload();
                Log.d("API Response", response);
                //todo: develop a mechanism to tell the app that server db is uodated

            }

            @Override
            public void onFailure(VolleyError error) {
                //todo: handle video deletion/updating the server whenever possible if not successful in first go
                //todo: what if the user changes the section id and lesson id during the download


                Log.d("API Response", error.toString());



            }
        };
        Map<String, String> params = new HashMap<>();
        params.put("auth_token", UserPreferences.getString(Constants.TOKEN));
        params.put("lesson_id", String.valueOf(lesson_id));

        params.put("status", String.valueOf("1"));

        APIClient myVolleyApiClient = new APIClient(this);
        myVolleyApiClient.saveDownloadProgress(Constants.url+"lesson_downloaded", params, listener);



    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

