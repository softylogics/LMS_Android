package com.dusre.lms.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.dusre.lms.Util.APIClient;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.adapters.SectionsAdapter;
import com.dusre.lms.databinding.CourseDetailLayoutBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Course;

import com.dusre.lms.model.Section;
import com.dusre.lms.viewmodel.SectionsViewModel;
import com.dusre.lms.viewmodel.CoursesViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionsFragment extends Fragment implements SetOnClickListener {

    private CourseDetailLayoutBinding binding;
    private SectionsAdapter sectionsAdapter;
    private List<Course> courseList;
    private CoursesViewModel coursesViewModel;
    private SectionsViewModel sectionsViewModel;
    private Gson gson;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = CourseDetailLayoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerViewCourseDetail.setLayoutManager(new LinearLayoutManager(requireContext()));

        coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
        sectionsViewModel = new ViewModelProvider(requireActivity()).get(SectionsViewModel.class);
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
    }

    @Override
    public void onLessonNameClick(int position) {
        //todo: complete this

    }
}