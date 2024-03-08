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
import com.dusre.lms.databinding.FragmentPlayerLayoutBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Course;

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

public class VideoPlayerFragment extends Fragment implements SetOnClickListener {

    private FragmentPlayerLayoutBinding binding;

    private List<Lesson> lessonList;
    private CoursesViewModel coursesViewModel;
    private SectionsViewModel sectionsViewModel;
    private LessonsViewModel lessonsViewModel;
    private Gson gson;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentPlayerLayoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
        sectionsViewModel = new ViewModelProvider(requireActivity()).get(SectionsViewModel.class);
        lessonsViewModel = new ViewModelProvider(requireActivity()).get(LessonsViewModel.class);

        // Initialize course list and adapter
        //todo: add if else for network data fetch


        gson = new Gson();
        // Populate course list (You may fetch it from database or API)

//        final TextView textView = binding.textHome;
//        myCourseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
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