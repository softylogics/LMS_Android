package com.dusre.lms.ui;

import android.content.Context;
import android.net.Uri;
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

import com.dusre.lms.MainActivity;
import com.dusre.lms.R;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.Util.DatabaseHelper;
import com.dusre.lms.adapters.DownloadedCourseAdapter;
import com.dusre.lms.databinding.FragmentDownloadedCoursesBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.DownloadedCourse;
import com.dusre.lms.model.DownloadedLesson;
import com.dusre.lms.model.DownloadedSection;
import com.dusre.lms.model.DownloadedVideo;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadedVideoFragment extends Fragment implements SetOnClickListener {


    private FragmentDownloadedCoursesBinding binding;
    private DownloadedCourseAdapter downloadedCourseAdapter;
    private List<DownloadedVideo> downloadedVideoList;
    private DownloadedVideoViewModel downloadedVideoViewModel;
    private Gson gson;
    private NavController navController;
    private DatabaseHelper dbHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDownloadedCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.rvDownloadedCourses.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        MainActivity activity = (MainActivity) requireActivity();
        downloadedVideoViewModel = new ViewModelProvider(requireActivity()).get(DownloadedVideoViewModel.class);

        dbHelper = new DatabaseHelper(getContext());
        downloadedCourseAdapter = new DownloadedCourseAdapter(requireContext(), downloadedVideoViewModel, this);
        callDBForDownloadedCourses();

        binding.rvDownloadedCourses.setAdapter(downloadedCourseAdapter);

        gson = new Gson();

        // Populate course list (You may fetch it from database or API)
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void callDBForDownloadedCourses() {

        downloadedVideoViewModel.setDownloadedCourses(getCourseWiseData(dbHelper.getAllDownloadedVideos()));
    }

    private List<DownloadedCourse> getCourseWiseData(List<DownloadedVideo> downloadedVideos) {

            List<DownloadedCourse> downloadedCourses = new ArrayList<>();

            // Map to store sections by their IDs
            Map<String, DownloadedSection> sectionMap = new HashMap<>();

            for (DownloadedVideo video : downloadedVideos) {
                // Find or create the section for the video
                DownloadedSection section = sectionMap.get(video.getSection_id());
                if (section == null) {
                    section = new DownloadedSection();
                    section.setSectionID(video.getSection_id());
                    section.setSectionTitle(video.getSection_title());
                    section.setDownloadedLessons(new ArrayList<>());
                    sectionMap.put(video.getSection_id(), section);
                }

                // Add the video as a lesson to the section
                DownloadedLesson lesson = new DownloadedLesson();
                lesson.setId(video.getId());
                lesson.setLessonTitle(video.getTitle());
                lesson.setDuration(video.getDuration());
                lesson.setVideoPath(video.getVideo_file_path());
                String filePath = video.getVideo_file_path();
                String validFilePath = filePath.replaceFirst("file://", "");
                File file = new File(validFilePath);
                if(file.exists()) {
                    section.getDownloadedLessons().add(lesson);
                }

            }

            // Map to store courses by their IDs
            Map<String, DownloadedCourse> courseMap = new HashMap<>();

            for (DownloadedVideo video : downloadedVideos) {
                // Find or create the course for the video
                DownloadedCourse course = courseMap.get(video.getCourse_id());
                if (course == null) {
                    course = new DownloadedCourse();
                    course.setCourseID(video.getCourse_id());
                    course.setCourseTitle(video.getCourse_title());
                    course.setDownloadedSections(new ArrayList<>());

                    courseMap.put(video.getCourse_id(), course);
                }

                // Find the section for the video
                DownloadedSection section = sectionMap.get(video.getSection_id());

                // Check if the section is already added to the course
                if (!course.getDownloadedSections().contains(section)) {
                    if(!section.getDownloadedLessons().isEmpty()) {
                        course.getDownloadedSections().add(section);
                    }
                }
            }

            // Add courses to the result list
            downloadedCourses.addAll(courseMap.values());

            return downloadedCourses;



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
        Constants.current_downloaded_course_id = position;
        navController.navigate(R.id.action_navigation_downloaded_courses_to_navigation_downloaded_course_details);
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

}
