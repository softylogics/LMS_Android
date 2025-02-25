package com.dusre.lms.ui;

import static android.content.Context.DOWNLOAD_SERVICE;



import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.dusre.lms.MainActivity;
import com.dusre.lms.R;
import com.dusre.lms.Util.APIClient;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.Util.DatabaseHelper;

import com.dusre.lms.Util.DownloadService;
import com.dusre.lms.Util.PostDownloadService;
import com.dusre.lms.Util.UserPreferences;
import com.dusre.lms.VideoPlayerActivity;
import com.dusre.lms.adapters.SectionsAdapter;
import com.dusre.lms.databinding.CourseDetailLayoutBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Course;

import com.dusre.lms.model.DownloadedVideo;
import com.dusre.lms.model.Lesson;
import com.dusre.lms.model.Section;
import com.dusre.lms.viewmodel.LessonsViewModel;
import com.dusre.lms.viewmodel.SectionsViewModel;
import com.dusre.lms.viewmodel.CoursesViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//todo: if user closes app during download
public class SectionsFragment extends Fragment implements SetOnClickListener {

    private CourseDetailLayoutBinding binding;
//    private List<Course> courseList;
    private CoursesViewModel coursesViewModel;
    private SectionsViewModel sectionsViewModel;

    private LessonsViewModel lessonsViewModel;
    private Gson gson;
//    private NavController navController;
//    private ProgressDialog pDialog;

    private DownloadedVideo downloadedVideo;
    private DatabaseHelper dbHelper;
    private DownloadManager downloadManager;
    private long downloadID = -1;


//    private File file;
    private BroadcastReceiver onDownloadComplete;
    private int lesson_id = -1;
    private BroadcastReceiver onDownloadNotoficationClicked;
    private boolean isFragmentAttached = false;
    private APIClient myVolleyApiClient;
    private boolean fetching = false;
    private NavController navController;
    private boolean updatingOnServer = false;
    private SectionsAdapter sectionsAdapter;
//    private DownloadCompleteReceiver downloadCompleteReceiver;

    public SectionsFragment() {
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = CourseDetailLayoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerViewCourseDetail.setLayoutManager(new LinearLayoutManager(requireContext()));
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
        sectionsViewModel = new ViewModelProvider(requireActivity()).get(SectionsViewModel.class);
        lessonsViewModel = new ViewModelProvider(requireActivity()).get(LessonsViewModel.class);
        // Initialize course list and adapter

        sectionsAdapter = new SectionsAdapter(requireContext(), sectionsViewModel, this);
        sectionsViewModel.getSections().setValue(null);

        Course course = coursesViewModel.getMyCourses().getValue().get(Constants.current_course_id);
        binding.txtCourseTitleDetail.setText(course.getTitle());
        binding.courseDetailCompletedLectures.setText(course.getTotal_number_of_completed_lessons() + "/" + course.getTotal_number_of_lessons());
        binding.courseDetailProgressBarLabel.setText(course.getCompletion()+"% Complete");
        binding.courseDetailProgressBar.setProgress(calculateProgress(course));
        binding.recyclerViewCourseDetail.setAdapter(sectionsAdapter);
        gson = new Gson();

        // Populate course list (You may fetch it from database or API)


        binding.swipeRefreshLayoutCourseDetail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(checkInternet()!=0) {
                    if(sectionsViewModel.getSections().getValue()==null) {
                        binding.progressBarCourseDetail.setVisibility(View.VISIBLE);
                        callAPIForCoursesSections();
                        binding.swipeRefreshLayoutCourseDetail.setRefreshing(false);
                    }
                    else{
                        binding.swipeRefreshLayoutCourseDetail.setRefreshing(false);
                    }
                }
                else{
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dbHelper = new DatabaseHelper(getContext());
        myVolleyApiClient = new APIClient(getContext());
//         downloadCompleteReceiver = new DownloadCompleteReceiver();
//        onDownloadComplete = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                //Fetching the download id received with the broadcast
//                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                //Checking if the received broadcast is for our enqueued download by matching download id
//                if (downloadID == id) {
//                    handleDownloadCompletion(context, downloadID);
////                    Log.d("download", "in if receiver");
////                    DownloadManager.Query query = new DownloadManager.Query();
////                    query.setFilterById(downloadID);
////
////                    Cursor cursor = downloadManager.query(query);
////                    if (cursor.moveToFirst()) {
////
////                        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
////                        int status = cursor.getInt(statusIndex);
////                        Log.d("download" , status+"");
////                        Log.d("download" , statusIndex+"");
////
////                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
////                            // Download completed successfully
////                            Log.d("download", "in if if receiver");
////                            String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
////                            cursor.close();
////                            downloadedVideo.setVideo_file_path(filePath);
////                            downloadedVideo.setUpdateOnServer("0");
////                            dbHelper.addDownloadedVideo(downloadedVideo);
////                            // Handle successful download
////                            Toast.makeText(getContext(), "Download Completed. You can find it in Downloaded lectures.", Toast.LENGTH_LONG).show();
////                            hideDownloadProgressBar();
////
////                            binding.progressBarCourseDetail.setVisibility(View.VISIBLE);
////                            ((MainActivity) requireActivity()).disableBottomNav();
////                            updateLessonOnServer();
////                        } else if (status == DownloadManager.STATUS_FAILED) {
////                            // Download failed
////                            // Handle download failure
////                            hideDownloadProgressBar();
////                            Toast.makeText(getContext(), "Download Failed", Toast.LENGTH_SHORT).show();
////                        } else if (status == DownloadManager.STATUS_PAUSED) {
////                            // Download paused
////                            // Handle download pause
////                            Toast.makeText(getContext(), "Download Paused", Toast.LENGTH_SHORT).show();
////                        }
////                        // Retrieve the file path
////
////                        // Store the file path for later access (e.g., in SharedPreferences)
////                        // Here, for simplicity, we'll just use a class-level variable
////
////                }
//                }
//                lesson_id = -1;
//                downloadID = -1;
//            }
//
//
//        };
//        requireActivity().registerReceiver(downloadCompleteReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        onDownloadNotoficationClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID != id) {

                        Toast.makeText(context, "Download Notification Clicked", Toast.LENGTH_SHORT).show();

                }
            }
        };
        requireActivity().registerReceiver(onDownloadNotoficationClicked,new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));


        return root;
    }

    private void handleDownloadCompletion(Context context, long downloadID) {
        // Perform actions after download completes (e.g., process the downloaded file)
        // You can start a service or perform any necessary background processing here
        // For example, launch a service to handle post-download tasks
        Intent serviceIntent = new Intent(context, PostDownloadService.class);
        serviceIntent.putExtra("downloadId", downloadID);
        serviceIntent.putExtra("lesson_id", lesson_id);
        context.startService(serviceIntent);
    }

    private void updateLessonOnServer() {
        updatingOnServer = true;
        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {


                //sectionsAdapter.reload();
                Log.d("API Response", response);
                ((MainActivity) requireActivity()).enableBottomNav();
                callAPIForCoursesSections();

                updatingOnServer = false;
            }

            @Override
            public void onFailure(VolleyError error) {
                //todo: handle video deletion/updating the server whenever possible if not successful in first go

                binding.progressBarCourseDetail.setVisibility(View.GONE);

                Log.d("API Response", error.toString());


                updatingOnServer = false;
            }
        };
        Map<String, String> params = new HashMap<>();
        params.put("auth_token", UserPreferences.getString(Constants.TOKEN));
        params.put("lesson_id", String.valueOf(lesson_id));

        params.put("status", String.valueOf("1"));

        myVolleyApiClient.saveDownloadProgress(Constants.url+"lesson_downloaded", params, listener);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        implementBackButtonFunctionality(view);

//        navController = Navigation.findNavController(view);
    }

    private int calculateProgress(Course course) {
        double m = (double) course.getTotal_number_of_completed_lessons()/course.getTotal_number_of_lessons() *100;

        return (int) m;
    }
    private void callAPIForCoursesSections() {
        fetching = true;


        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response
                if(isFragmentAttached) {
                    binding.progressBarCourseDetail.setVisibility(View.GONE);
                    sectionsViewModel.getSections().setValue(parseJsonToCourseList(response));
                    Log.d("API Response", response);
                    ((MainActivity) requireActivity()).enableBottomNav();
                    fetching = false;
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                // Handle failure
                if(isFragmentAttached) {
                    binding.progressBarCourseDetail.setVisibility(View.GONE);
                    Log.d("API Response", error.toString());
                    ((MainActivity) requireActivity()).enableBottomNav();
                    fetching = false;
                }
            }
        };


        Map<String, String> params = new HashMap<>();
        params.put("auth_token", UserPreferences.getString(Constants.TOKEN));
        params.put("course_id", String.valueOf(coursesViewModel.getMyCourses().getValue().get(Constants.current_course_id).id));

        myVolleyApiClient.fetchDataFromApi(Constants.url+"sections", params, listener, Constants.SECTIONS_FRAGMENT);
    }

    public List<Section> parseJsonToCourseList(String jsonString) {
        Type listType = new TypeToken<List<Section>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
//        getActivity().unregisterReceiver(downloadCompleteReceiver);
    }

    @Override
    public void onItemClickCourse(int position) {

    }

    @Override
    public void onItemClickCourseSection(int position) {

    }

    @Override
    public void onCheckBoxClick(int position) {

    }

    @Override
    public void onDownloadButtonClick(int position) {

        lessonsViewModel.setMyLessons(sectionsViewModel.getSections().getValue().get(Constants.current_section_id).getLessons());
//        if(hasPermissionToDownload(getActivity())){
            if(checkInternet()!=0) {
//                String videoId = UUID.randomUUID().toString();

//                downloadedVideo = new DownloadedVideo();
//                downloadedVideo.setId(videoId);
//                downloadedVideo.setTitle(lesson.getTitle());
//                downloadedVideo.setDuration(lesson.getDuration());
//                downloadedVideo.setCourse_id(lesson.getCourse_id());
//                downloadedVideo.setCourse_title(getCourseTitle(lesson.getCourse_id()));
//                downloadedVideo.setSection_id(lesson.getSection_id());
//                downloadedVideo.setSection_title(getSectionTitle(lesson.getSection_id()));
//                downloadedVideo.setUpdateOnServer("0");
                if(!Constants.downloading) {
                    Constants.downloading = true;
                    Lesson lesson = lessonsViewModel.getMyLessons().getValue().get(position);
                    lesson_id = Integer.parseInt(lesson.getId());
                    UserPreferences.setInt(Constants.lesson_id_for_post_download_service, lesson_id);
                    UserPreferences.setString(Constants.course_id_for_post_download_service, lesson.getCourse_id());
                    Intent serviceIntent = new Intent(getContext(), DownloadService.class);
                    serviceIntent.putExtra("url", lesson.getVideo_url_web());
                    requireActivity().startService(serviceIntent);
                }
                else{
                    Toast.makeText(getContext(), "Already Downloading", Toast.LENGTH_SHORT).show();
                }
//                beginDownload(lesson.getVideo_url_web());

            }
            else{
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
//        }
    }


private void beginDownload(String url){
    if (downloadID == -1) {
//        showDownloadProgressBar();
        String fileName = url.substring(url.lastIndexOf('/') + 1);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))

                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationInExternalFilesDir(getContext(),null, fileName)
                .setTitle(fileName)// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

        downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        Toast.makeText(getContext(), "Download started..." , Toast.LENGTH_LONG).show();

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
    }
    else{
        Toast.makeText(getContext(), "Already Downloading", Toast.LENGTH_SHORT).show();
    }

    }

    private void showDownloadProgressBar(){

        binding.downloadProgressLayout.setVisibility(View.VISIBLE);
        ((MainActivity) requireActivity()).disableBottomNav();
        binding.recyclerViewCourseDetail.setEnabled(false);
        binding.recyclerViewCourseDetail.setAlpha(0.3f);
    }

    private void hideDownloadProgressBar(){
        binding.downloadProgressLayout.setVisibility(View.GONE);
        ((MainActivity) requireActivity()).enableBottomNav();
        binding.recyclerViewCourseDetail.setEnabled(true);
        binding.recyclerViewCourseDetail.setAlpha(1f);
    }
    private void updateProgressDialog(int progress) {
        // Update your custom progress dialog with the progress value
        binding.downloadProgressBarCourseDetail.setProgress(progress);
        binding.txtDownloadingProgress.setText(progress+"%");
    }

    private String getCourseTitle(String courseId) {

        for(Course course: coursesViewModel.getMyCourses().getValue()){
            if(course.getId().equals(courseId)){
                return course.getTitle();
            }
        }
        return null;
    }

    private String getSectionTitle(String sectionId) {
        for(Section section: sectionsViewModel.getSections().getValue()){
            if(section.getId().equals(sectionId)){
                return section.getTitle();
            }
        }
        return null;
    }

    @Override
    public void onLessonNameClick(int position) {

        Constants.isDownloadVideoPlay = false;
        Constants.current_lesson_id = position;
        lessonsViewModel.setMyLessons(sectionsViewModel.getSections().getValue().get(Constants.current_section_id).getLessons());
        Constants.lessons = sectionsViewModel.getSections().getValue().get(Constants.current_section_id).getLessons();
        startActivity(new Intent(getActivity(), VideoPlayerActivity.class));
//        navController.navigate(R.id.action_navigation_sections_to_navigation_video_player);

    }

    @Override
    public void onNextLessonClick(int position) {

    }

    @Override
    public void onDownloadedItemClickCourse(int position) {

    }

    @Override
    public void onDownloadedLessonNameClick(int position) {

    }

    @Override
    public void onDownloadedNextLessonClick(int position) {

    }

    @Override
    public void onDownloadDeleteVideo(int position) {

    }

    public static boolean hasPermissionToDownload(final Activity context) {
        if (Build.VERSION.SDK_INT >= 29 || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(R.string.download_permission_explaination);
        builder.setPositiveButton(R.string.download_permission_grant, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Fire off an async request to actually get the permission
                // This will show the standard permission request dialog UI
                context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

//    class DownloadFileFromURL extends AsyncTask<String, String, String> {
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showDialog();
//        }
//
//
//        @Override
//        protected String doInBackground(String... f_url) {
//            int count;
//            File file = null;
//            try {
//                URL url = new URL(f_url[0]);
//
//                String decodedURL = java.net.URLDecoder.decode(f_url[0], "UTF-8");
//                Log.d("url", decodedURL);
//                String name = decodedURL.substring(f_url[0].lastIndexOf("/"));
//                URLConnection connection = url.openConnection();
//                connection.connect();
//                // getting file length
//                int lenghtOfFile = connection.getContentLength();
//
//                // input stream to read file - with 8k buffer
//                InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//                // Specify the folder name where you want to save files
//                String folderName = "DownloadedVideos";
//
//// Get the path to the directory in the app's internal storage
//                File directory = new File(getActivity().getFilesDir(), folderName);
//
//// Check if the directory already exists, if not, create it
//                if (!directory.exists()) {
//                    if (directory.mkdirs()) {
//                        // Directory created successfully or already exists
//                        // Now you can save files into this directory
//                        // Create a new file in the specified directory
//                            file = new File(directory, name);
//
//                        // Output stream to write file
//                        OutputStream output = new FileOutputStream(file);
//
//                        byte data[] = new byte[1024];
//
//                        long total = 0;
//
//                        while ((count = input.read(data)) != -1) {
//                            total += count;
//                            // publishing the progress....
//                            // After this onProgressUpdate will be called
//                            publishProgress(""+(int)((total*100)/lenghtOfFile));
//
//                            // writing data to file
//                            output.write(data, 0, count);
//                        }
//
//                        // flushing output
//                        output.flush();
//
//                        // closing streams
//                        output.close();
//                        input.close();
//                    } else {
//                        // Failed to create directory
//                        // Handle this case according to your app's logic
//                    }
//                } else {
//                    // Directory already exists
//                    // You can proceed to save files into this directory
//                    // Create a new file in the specified directory
//                    file = new File(directory, name);
//
//                    // Output stream to write file
//                    OutputStream output = new FileOutputStream(file);
//
//                    byte data[] = new byte[1024];
//
//                    long total = 0;
//
//                    while ((count = input.read(data)) != -1) {
//                        total += count;
//                        // publishing the progress....
//                        // After this onProgressUpdate will be called
//                        publishProgress(""+(int)((total*100)/lenghtOfFile));
//
//                        // writing data to file
//                        output.write(data, 0, count);
//                    }
//
//                    // flushing output
//                    output.flush();
//
//                    // closing streams
//                    output.close();
//                    input.close();
//                }
//
//
//
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//            }
//            if(file!=null) {
//                return file.getAbsolutePath();
//            }
//            else{
//                return null;
//            }
//        }
//
//        /**
//         * Updating progress bar
//         * */
//        protected void onProgressUpdate(String... progress) {
//            // setting progress percentage
//            pDialog.setProgress(Integer.parseInt(progress[0]));
//        }
//
//        /**
//         * After completing background task
//         * Dismiss the progress dialog
//         * **/
//        @Override
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog after the file was downloaded
//
//
//            // Find the index of "/files/"
//            int filesIndex = file_url.indexOf("/files/");
//
//                // Extract the substring after "/files/"
//                String substringAfterFiles = file_url.substring(filesIndex + "/files/".length());
//
//
//            downloadedVideo.setVideo_file_path(substringAfterFiles);
//            dbHelper.addDownloadedVideo(downloadedVideo);
//            pDialog.dismiss();
//
//        }
//        @Override
//        protected void onCancelled(String file_url) {
//            // If the download was cancelled, you can clean up resources here
//            // Delete the partially downloaded file if it exists
//            if (file_url != null) {
//                File file = new File(file_url);
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//            dbHelper.deleteDownloadedVideo(videoId);
//            // Dismiss the progress dialog
//            pDialog.dismiss();
//        }
//
//    }

    private int checkInternet() {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isFragmentAttached = true;


    }

    @Override
    public void onStop() {
        super.onStop();
        isFragmentAttached = false;


    }

    @Override
    public void onStart() {
        super.onStart();
        if(checkInternet()!=0) {
            if(sectionsViewModel.getSections().getValue()==null) {
                ((MainActivity) requireActivity()).disableBottomNav();
                binding.progressBarCourseDetail.setVisibility(View.VISIBLE);

                callAPIForCoursesSections();
            }
        }
        else{
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void implementBackButtonFunctionality(View view) {


        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (fetching) {
                    Toast.makeText(getContext(), "Please wait for the process to complete." , Toast.LENGTH_SHORT).show();
                } else {
                    navController.navigateUp();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


    }

}