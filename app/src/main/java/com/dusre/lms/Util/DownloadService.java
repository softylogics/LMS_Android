package com.dusre.lms.Util;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.dusre.lms.model.CourseForPostDownloadService;
import com.dusre.lms.model.DownloadedVideo;
import com.dusre.lms.model.Lesson;
import com.dusre.lms.model.Section;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class DownloadService extends Service {
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return DownloadService.this;
        }
    }
    // BroadcastReceiver to handle download completion
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check if the download was successful
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                // Handle the completed download (e.g., perform post-download tasks)
                UserPreferences.init(getApplicationContext());
                Log.d("post Download" , " in service");
//                long downloadID = intent.getLongExtra("downloadId", -1);

                // Handle post-download tasks (e.g., file processing, database update)
                // You can retrieve the file using the downloadId and perform necessary operations
                Log.d("download", "in if receiver");
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadID);
                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {

                    int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(statusIndex);
                    Log.d("download" , status+"");
                    Log.d("download" , statusIndex+"");

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // Download completed successfully
                        Log.d("download", "successful");
                        String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));


                        cursor.close();
                        callCourse(filePath);

                    } else if (status == DownloadManager.STATUS_FAILED) {
                        // Download failed
                        // Handle download failure
                        Log.d("post Download" , "failed");
                        Toast.makeText(getApplicationContext(), "Download Failed", Toast.LENGTH_SHORT).show();
                        UserPreferences.setInt(Constants.lesson_id_for_post_download_service, -1);
                        UserPreferences.setString(Constants.course_id_for_post_download_service, null);
                        Constants.downloading = false;



                        stopSelf();
                    } else if (status == DownloadManager.STATUS_PAUSED) {
                        // Download paused
                        Log.d("post Download" , "paused");
                        // Handle download pause
                        Toast.makeText(getApplicationContext(), "Download Paused", Toast.LENGTH_SHORT).show();
                    }
                    // Retrieve the file path

                    // Store the file path for later access (e.g., in SharedPreferences)
                    // Here, for simplicity, we'll just use a class-level variable

                }
            }
        }
    };
    private long downloadID = -1;
    private Lesson requiredLesson;
    private String sectionTitle;
    private String courseTitle;
    private DownloadManager downloadManager;
    private FirebaseCrashlytics crashlytics;
    private boolean taskRemovedCalled = false;


    @Override
    public void onCreate() {
        super.onCreate();
        crashlytics = FirebaseCrashlytics.getInstance();
        // Register the receiver for download completion
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private Notification createNotification() {
        return null;
    }

    private void callCourse(String filePath) {
    //todo: develop retry policy
        APIClient myVolleyApiClient = new APIClient(this);

        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response
                List<CourseForPostDownloadService> course = parseCourseJson(response);
                for(Section section : course.get(0).getSections()){
                    for(Lesson lesson : section.getLessons()){
                        if(lesson.getId().equals(String.valueOf(UserPreferences.getint(Constants.lesson_id_for_post_download_service)))){
                            requiredLesson = lesson;
                            sectionTitle = section.getTitle();
                            courseTitle = course.get(0).getTitle();
                        }
                    }
                }
                updateLessonOnServer(UserPreferences.getint(Constants.lesson_id_for_post_download_service), filePath);
                Log.d("API Response", response);
            }

            @Override
            public void onFailure(VolleyError error) {
                // Handle failure
//                if(isFragmentAttached) {
                UserPreferences.setInt(Constants.lesson_id_for_post_download_service, -1);
                UserPreferences.setString(Constants.course_id_for_post_download_service, null);
                Toast.makeText(getApplicationContext(), "An error occurred downloading the lecture", Toast.LENGTH_LONG).show();
                Log.d("API Response", error.toString());

//                }
            }
        };


        Map<String, String> params = new HashMap<>();
        params.put("auth_token", UserPreferences.getString(Constants.TOKEN));
        params.put("course_id", UserPreferences.getString(Constants.course_id_for_post_download_service));

        myVolleyApiClient.fetchDataFromApi(Constants.url+"course_details_by_id", params, listener , Constants.MY_COURSE_FRAGMENT);

    }
    public List<CourseForPostDownloadService> parseCourseJson(String json) {
        Type courseListType = new TypeToken<List<CourseForPostDownloadService>>(){}.getType();
        List<CourseForPostDownloadService> courses = new Gson().fromJson(json, courseListType);
        return courses;

    }

    private void updateLessonOnServer(int lesson_id, String filePath) {

        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                String videoId = UUID.randomUUID().toString();
                DownloadedVideo downloadedVideo = new DownloadedVideo();

                downloadedVideo.setId(videoId);
                downloadedVideo.setTitle(requiredLesson.getTitle());
                downloadedVideo.setDuration(requiredLesson.getDuration());
                downloadedVideo.setCourse_id(requiredLesson.getCourse_id());
                downloadedVideo.setCourse_title(courseTitle);
                downloadedVideo.setSection_id(requiredLesson.getSection_id());
                downloadedVideo.setSection_title(sectionTitle);
                downloadedVideo.setUpdateOnServer("0");
                Log.d("downloadUpdateLesson" , filePath);
                downloadedVideo.setVideo_file_path(filePath);
                downloadedVideo.setUpdateOnServer("0");

                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.addDownloadedVideo(downloadedVideo);
                Toast.makeText(getApplicationContext(), "Download Successful, you can find it in Downloaded lectures.", Toast.LENGTH_LONG).show();
                UserPreferences.setInt(Constants.lesson_id_for_post_download_service, -1);
                UserPreferences.setString(Constants.course_id_for_post_download_service, null);
                Constants.downloading = false;
                //sectionsAdapter.reload();
                Log.d("API Response", response);
                //todo: develop a mechanism to tell the app that server db is updated
                stopSelf();
            }

            @Override
            public void onFailure(VolleyError error) {
                UserPreferences.setInt(Constants.lesson_id_for_post_download_service, -1);
                UserPreferences.setString(Constants.course_id_for_post_download_service, null);
                Constants.downloading = false;
                Toast.makeText(getApplicationContext(), "An error occurred downloading the lecture", Toast.LENGTH_LONG).show();
                downloadManager.remove(downloadID);
                stopSelf();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Extract the URL from the intent
        Log.d("post download", "service started" );
        String downloadUrl = intent.getStringExtra("url");
        if (downloadUrl != null) {
            startDownload(downloadUrl);
        }
        return START_NOT_STICKY;
    }

    private void startDownload(String url) {
        Log.d("post download", "download start" );
        Log.d("download" , url);
//        showDownloadProgressBar();
            String fileName = url.substring(url.lastIndexOf('/') + 1);
        Log.d("downloadStartDownload" , getApplicationContext().getFilesDir().getAbsolutePath()+fileName);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))

                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                    .setDestinationInExternalFilesDir(getApplicationContext(), getApplicationContext().getFilesDir().getAbsolutePath(), fileName)
                    .setTitle(fileName)// Title of the Download Notification
                    .setDescription("Downloading")// Description of the Download Notification
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                    .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

            downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
            Toast.makeText(this, "Download will start soon" , Toast.LENGTH_LONG).show();

//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
//                if (cursor.moveToFirst()) {
//                    int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                    int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                    int progress = (int) ((bytesDownloaded * 100L) / bytesTotal);
//
//
//                    // Update your custom progress dialog with the progress value
//                    updateProgressDialog(progress);
//
//                    if (progress < 100) {
//                        handler.postDelayed(this, 1000); // Poll every second
//                    }
//                }
//                cursor.close();
//            }
//        };
//        handler.post(runnable);

        // Save the download ID to track the download
    }



    @Override
    public void onDestroy() {
        Log.d("service", "service destroyed");
        //downloadManager.remove(downloadID);
        // Unregister the receiver when the service is destroyed
        crashlytics.setCustomKey("taskRemovedCalled", taskRemovedCalled);
        cancelDownload();
       unregisterMyBroadcastReceiver();
        super.onDestroy();
    }
    private void unregisterMyBroadcastReceiver() {
        if (null != downloadReceiver) {
            unregisterReceiver(downloadReceiver);
            downloadReceiver = null;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder; // We don't provide binding, so return null
    }
    public void cancelDownload() {
        if(Constants.downloading) {
            Log.d("service" , "cancel download");
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.remove(downloadID); // Cancel the download
            Constants.downloading = false;
        }
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("service" , "taskRemovedCalled");
        taskRemovedCalled = true;
        cancelDownload();
        unregisterMyBroadcastReceiver();
        stopSelf();
    }


}

