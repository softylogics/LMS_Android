package com.dusre.lms.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.dusre.lms.MainActivity;
import com.dusre.lms.R;
import com.dusre.lms.Util.APIClient;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.Util.UserPreferences;
import com.dusre.lms.adapters.MyCoursesAdapter;
import com.dusre.lms.databinding.FragmentMyCourseBinding;
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
    private List<Course> courseList;
    private CoursesViewModel coursesViewModel;
    private Gson gson;
    private NavController navController;

    private boolean isFragmentAttached = false;
    private APIClient myVolleyApiClient;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMyCourseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        UserPreferences.init(getContext());
        binding.recyclerViewMyCourses.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
        // Initialize course list and adapter
        courseList = new ArrayList<>();
        MyCoursesAdapter courseAdapter = new MyCoursesAdapter(requireContext(), courseList, coursesViewModel, this);


        binding.recyclerViewMyCourses.setAdapter(courseAdapter);
        gson = new Gson();


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(courseList.isEmpty()) {
                    Log.d("refresh" , "in refresh");
                    binding.progressBar.setVisibility(View.VISIBLE);
                    callAPIForCourses();
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

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
    private void callAPIForCourses() {
        myVolleyApiClient = new APIClient(getContext());

        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response
//                if(isFragmentAttached) {
                    binding.progressBar.setVisibility(View.GONE);
                    coursesViewModel.setMyCourses(parseJsonToCourseList(response));
                    Log.d("API Response", response);
                ((MainActivity) requireActivity()).enableBottomNav();
//                }
            }

            @Override
            public void onFailure(VolleyError error) {
                // Handle failure
//                if(isFragmentAttached) {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.d("API Response", error.toString());
                ((MainActivity) requireActivity()).enableBottomNav();
//                }
            }
        };


        Map<String, String> params = new HashMap<>();
        params.put("auth_token", UserPreferences.getString(Constants.TOKEN));

        myVolleyApiClient.fetchDataFromApi(Constants.url+"my_courses", params, listener , Constants.MY_COURSE_FRAGMENT);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isFragmentAttached = true;


    }

    @Override
    public void onPause() {
        super.onPause();

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
            if(courseList.isEmpty()) {
                ((MainActivity) requireActivity()).disableBottomNav();
                binding.progressBar.setVisibility(View.VISIBLE);
                if(!UserPreferences.getBoolean(Constants.ALREADY_INSTALLED)){
                    resetDownloadsOnServer();
                }
                else {
                    callAPIForCourses();
                }
            }
        }
        else{
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetDownloadsOnServer() {
        myVolleyApiClient = new APIClient(getContext());

        APIClient.ApiResponseListener listener = new APIClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {

                Log.d("API Response", response);
                UserPreferences.setBoolean(Constants.ALREADY_INSTALLED, true);
                callAPIForCourses();
            }

            @Override
            public void onFailure(VolleyError error) {

                Log.d("API Response", error.toString());
                resetDownloadsOnServer();
            }
        };


        Map<String, String> params = new HashMap<>();
        params.put("auth_token", UserPreferences.getString(Constants.TOKEN));

        myVolleyApiClient.fetchDataFromApi(Constants.url+"downloaded_lesson_reset", params, listener , Constants.MY_COURSE_FRAGMENT);
    }
}