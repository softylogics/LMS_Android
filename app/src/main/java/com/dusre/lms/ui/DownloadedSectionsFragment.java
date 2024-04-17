package com.dusre.lms.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dusre.lms.Util.Constants;
import com.dusre.lms.Util.DatabaseHelper;
import com.dusre.lms.VideoPlayerActivity;
import com.dusre.lms.adapters.DownloadedSectionsAdapter;
import com.dusre.lms.databinding.DownloadedCourseDetailLayoutBinding;
import com.dusre.lms.listeners.SetOnClickListener;

import com.dusre.lms.model.DownloadedCourse;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;
import com.google.gson.Gson;

public class DownloadedSectionsFragment extends Fragment implements SetOnClickListener {

    private DownloadedCourseDetailLayoutBinding binding;
    private DownloadedSectionsAdapter downloadedSectionsAdapter;

    private DownloadedVideoViewModel downloadedVideoViewModel;
      private Gson gson;
    private NavController navController;


    private DatabaseHelper dbHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = DownloadedCourseDetailLayoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerViewDownloadedCourseDetail.setLayoutManager(new LinearLayoutManager(requireContext()));

        downloadedVideoViewModel = new ViewModelProvider(requireActivity()).get(DownloadedVideoViewModel.class);
        // Initialize course list and adapter

        downloadedSectionsAdapter = new DownloadedSectionsAdapter(requireContext(), downloadedVideoViewModel, this);
        DownloadedCourse downloadedVideo = downloadedVideoViewModel.getDownloadedCourses().getValue().get(Constants.current_downloaded_course_id);
        binding.txtDownloadedCourseTitleDetail.setText(downloadedVideo.getCourseTitle());
        binding.recyclerViewDownloadedCourseDetail.setAdapter(downloadedSectionsAdapter);
        gson = new Gson();
        // Populate course list (You may fetch it from database or API)
        dbHelper = new DatabaseHelper(getContext());
      return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
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

        Constants.isDownloadVideoPlay = true;
        Constants.current_downloaded_lesson_id = position;
        Constants.downloadedLessons = downloadedVideoViewModel.getDownloadedCourses().getValue().get(Constants.current_downloaded_course_id).getDownloadedSections().get(Constants.current_downloaded_section_id).getDownloadedLessons();
        startActivity(new Intent(getActivity(), VideoPlayerActivity.class));

//        navController.navigate(R.id.action_navigation_downloaded_sections_to_navigation_video_player);

    }

    @Override
    public void onDownloadedNextLessonClick(int position) {

    }

    @Override
    public void onDownloadDeleteVideo(int position) {
        //todo: complete this code

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

}