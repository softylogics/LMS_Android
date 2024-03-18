package com.dusre.lms.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.dusre.lms.R;
import com.dusre.lms.Util.APIClient;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.adapters.SectionsAdapter;
import com.dusre.lms.databinding.CourseDetailLayoutBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Course;

import com.dusre.lms.model.Section;
import com.dusre.lms.viewmodel.LessonsViewModel;
import com.dusre.lms.viewmodel.SectionsViewModel;
import com.dusre.lms.viewmodel.CoursesViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionsFragment extends Fragment implements SetOnClickListener {

    private CourseDetailLayoutBinding binding;
    private SectionsAdapter sectionsAdapter;
    private List<Course> courseList;
    private CoursesViewModel coursesViewModel;
    private SectionsViewModel sectionsViewModel;

    private LessonsViewModel lessonsViewModel;
    private Gson gson;
    private NavController navController;
    private ProgressDialog pDialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = CourseDetailLayoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerViewCourseDetail.setLayoutManager(new LinearLayoutManager(requireContext()));

        coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
        sectionsViewModel = new ViewModelProvider(requireActivity()).get(SectionsViewModel.class);
        lessonsViewModel = new ViewModelProvider(requireActivity()).get(LessonsViewModel.class);
        // Initialize course list and adapter
        //todo: add if else for network data fetch
        sectionsAdapter = new SectionsAdapter(requireContext(), sectionsViewModel, this);
        Course course = coursesViewModel.getMyCourses().getValue().get(Constants.current_course_id);
        binding.txtCourseTitleDetail.setText(course.getTitle());
        binding.courseDetailCompletedLectures.setText(course.getTotal_number_of_completed_lessons()+"/"+course.getTotal_number_of_lessons());
        binding.courseDetailProgressBarLabel.setText(course.getCompletion()+"% Complete");
        binding.courseDetailProgressBar.setProgress(calculateProgress(course));
        binding.recyclerViewCourseDetail.setAdapter(sectionsAdapter);
        gson = new Gson();
        // Populate course list (You may fetch it from database or API)
        callAPIForCoursesSections();
//        final TextView textView = binding.textHome;
//        myCourseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private int calculateProgress(Course course) {
        double m = (double) course.getTotal_number_of_completed_lessons()/course.getTotal_number_of_lessons() *100;

        return (int) m;
    }
    private void callAPIForCoursesSections() {
        APIClient myVolleyApiClient = new APIClient(getContext());

        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response


                sectionsViewModel.getSections().setValue(parseJsonToCourseList(response));
                Log.d("API Response", response);
            }

            @Override
            public void onFailure(VolleyError error) {
                // Handle failure
                Log.d("API Response", error.toString());
            }
        };


        Map<String, String> params = new HashMap<>();
        params.put("auth_token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiMTY2MyIsImZpcnN0X25hbWUiOiJNaXJ6YSIsImxhc3RfbmFtZSI6IlRlc3QiLCJlbWFpbCI6Im1pcnphQHRlc3QuY29tIiwicm9sZSI6InVzZXIiLCJ2YWxpZGl0eSI6MX0.WDmRKPtUJNN0WKXdEbBhNg-zpAEE2sMWNqvrFdw_gV4");
        params.put("course_id", String.valueOf(coursesViewModel.getMyCourses().getValue().get(Constants.current_course_id).id));

        myVolleyApiClient.fetchDataFromApi(Constants.url+"sections", params, listener);
    }
    public List<Section> parseJsonToCourseList(String jsonString) {
        Type listType = new TypeToken<List<Section>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickCourse(int position) {

    }

    @Override
    public void onItemClickCourseSection(int position) {

    }

    @Override
    public void onCheckBoxClick(int position) {
        //todo: complete this checkbox functionality
    }

    @Override
    public void onDownloadButtonClick(int position) {
        //todo: complete download functionality

        if(hasPermissionToDownload(getActivity())){
            new DownloadFileFromURL().execute(lessonsViewModel.getMyLessons().getValue().get(position).getVideo_url_web());

        }
    }

    @Override
    public void onLessonNameClick(int position) {
        //todo: complete this
        Constants.current_lesson_id = position;
        lessonsViewModel.setMyLessons(sectionsViewModel.getSections().getValue().get(position).getLessons());

        navController.navigate(R.id.action_navigation_sections_to_navigation_video_player);

    }

    @Override
    public void onNextLessonClick(int position) {

    }
    public static boolean hasPermissionToDownload(final Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= 29 ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED )
            return true;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(R.string.download_permission_explaination);
        builder.setPositiveButton(R.string.download_permission_grant, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Fire off an async request to actually get the permission
                // This will show the standard permission request dialog UI
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }
    public void showDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    class DownloadFileFromURL extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }


        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);

                String decodedURL = java.net.URLDecoder.decode(f_url[0], "UTF-8");
                Log.d("url", decodedURL);
                String name = decodedURL.substring(f_url[0].lastIndexOf("/"));
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lenghtOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Specify the folder name where you want to save files
                String folderName = "DownloadedVideos";

// Get the path to the directory in the app's internal storage
                File directory = new File(getActivity().getFilesDir(), folderName);

// Check if the directory already exists, if not, create it
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        // Directory created successfully or already exists
                        // Now you can save files into this directory
                        // Create a new file in the specified directory
                        File file = new File(directory, name);

                        // Output stream to write file
                        OutputStream output = new FileOutputStream(file);

                        byte data[] = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            publishProgress(""+(int)((total*100)/lenghtOfFile));

                            // writing data to file
                            output.write(data, 0, count);
                        }

                        // flushing output
                        output.flush();

                        // closing streams
                        output.close();
                        input.close();
                    } else {
                        // Failed to create directory
                        // Handle this case according to your app's logic
                    }
                } else {
                    // Directory already exists
                    // You can proceed to save files into this directory
                    // Create a new file in the specified directory
                    File file = new File(directory, name);

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(file);

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress(""+(int)((total*100)/lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                }



            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

        }

    }


}