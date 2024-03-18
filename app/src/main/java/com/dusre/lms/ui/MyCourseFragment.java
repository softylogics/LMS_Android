package com.dusre.lms.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.dusre.lms.MainActivity;
import com.dusre.lms.R;
import com.dusre.lms.Util.APIClient;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.databinding.FragmentMyCourseBinding;
import com.dusre.lms.adapters.MyCoursesAdapter;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Course;

import com.dusre.lms.viewmodel.CoursesViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCourseFragment extends Fragment implements SetOnClickListener {

    private FragmentMyCourseBinding binding;
    private MyCoursesAdapter courseAdapter;
    private List<Course> courseList;
    private CoursesViewModel coursesViewModel;
    private Gson gson;
    private NavController navController;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMyCourseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recyclerViewMyCourses.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        MainActivity activity = (MainActivity) requireActivity();
        coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
        // Initialize course list and adapter
        courseList = new ArrayList<>(); //todo: add if else for network data fetch
        courseAdapter = new MyCoursesAdapter(requireContext(), courseList, coursesViewModel, this);

        binding.recyclerViewMyCourses.setAdapter(courseAdapter);
        gson = new Gson();
        // Populate course list (You may fetch it from database or API)
        callAPIForCourses();
//        final TextView textView = binding.textHome;
//        myCourseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void callAPIForCourses() {
        APIClient myVolleyApiClient = new APIClient(getContext());

        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response


                coursesViewModel.setMyCourses(parseJsonToCourseList(response));
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

        myVolleyApiClient.fetchDataFromApi(Constants.url+"my_courses", params, listener);
    }
    public List<Course> parseJsonToCourseList(String jsonString) {
        Type listType = new TypeToken<List<Course>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickCourse(int position) {
        Constants.current_course_id = position;
        navController.navigate(R.id.action_navigation_my_courses_to_navigation_course_details);
    }

    @Override
    public void onItemClickCourseSection(int postion) {

    }

    @Override
    public void onCheckBoxClick(int position) {

    }

    @Override
    public void onDownloadButtonClick(int position) {

    }

    @Override
    public void onLessonNameClick(int position) {

    }

    @Override
    public void onNextLessonClick(int position) {

    }
}