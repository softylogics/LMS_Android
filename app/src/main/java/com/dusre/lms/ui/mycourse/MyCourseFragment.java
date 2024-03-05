package com.dusre.lms.ui.mycourse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dusre.lms.databinding.FragmentMyCourseBinding;
import com.dusre.lms.adapters.MyCoursesAdapter;
import com.dusre.lms.model.Course;

import java.util.ArrayList;
import java.util.List;

public class MyCourseFragment extends Fragment {

    private FragmentMyCourseBinding binding;
    private MyCoursesAdapter courseAdapter;
    private List<Course> courseList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyCourseViewModel myCourseViewModel =
                new ViewModelProvider(this).get(MyCourseViewModel.class);

        binding = FragmentMyCourseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.recyclerViewMyCourses.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Initialize course list and adapter
        courseList = new ArrayList<>();
        courseAdapter = new MyCoursesAdapter(requireContext(), courseList);
        binding.recyclerViewMyCourses.setAdapter(courseAdapter);

        // Populate course list (You may fetch it from database or API)
        populateCourseList();
//        final TextView textView = binding.textHome;
//        myCourseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    private void populateCourseList() {
        // Dummy data for demonstration
        courseList.add(new Course("Course 1", 4.5f, "50%", 50, "10/20"));
        courseList.add(new Course("Course 2", 3.8f, "30%", 30, "6/20"));
        courseList.add(new Course("Course 3", 4.2f, "70%", 70, "14/20"));

        // Notify adapter about data change
        courseAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}