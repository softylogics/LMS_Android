package com.dusre.lms.ui;

import static android.content.Context.DOWNLOAD_SERVICE;



import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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


//        onDownloadNotoficationClicked = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                if (downloadID == id) {
//
//                        Toast.makeText(context, "Download Notification Clicked", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        };
//        requireActivity().registerReceiver(onDownloadNotoficationClicked,new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));


        return root;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        implementBackButtonFunctionality(view);
        Course course = Objects.requireNonNull(coursesViewModel.getMyCourses().getValue()).get(Constants.current_course_id);
        binding.txtCourseTitleDetail.setText(course.getTitle());
        binding.courseDetailCompletedLectures.setText(course.getTotal_number_of_completed_lessons() + "/" + course.getTotal_number_of_lessons());
        binding.courseDetailProgressBarLabel.setText(course.getCompletion()+"% Complete");
        binding.courseDetailProgressBar.setProgress(calculateProgress(course));
        binding.recyclerViewCourseDetail.setAdapter(sectionsAdapter);

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
                    Intent intent = new Intent(getContext(), DownloadService.class);
                    requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                }
                else{

                    //mService.cancelDownload();
                    Toast.makeText(getContext(), "Already Downloading", Toast.LENGTH_SHORT).show();
                }
//                beginDownload(lesson.getVideo_url_web());

            }
            else{
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
//        }
    }

    private DownloadService mService;
    private boolean mBound = false;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
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
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);


    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("fragment", "SectionsFragment");

    }
}