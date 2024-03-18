package com.dusre.lms.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.dusre.lms.R;
import com.dusre.lms.Util.APIClient;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.adapters.NextVideoAdapter;
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
    private ExoPlayer player;
    private NextVideoAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentPlayerLayoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
        sectionsViewModel = new ViewModelProvider(requireActivity()).get(SectionsViewModel.class);
        lessonsViewModel = new ViewModelProvider(requireActivity()).get(LessonsViewModel.class);
        initializePlayer();
        binding.recyclerViewNextVideos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NextVideoAdapter(getContext(), lessonsViewModel, this);
        binding.recyclerViewNextVideos.setAdapter(adapter);
        // Initialize course list and adapter
        //todo: add if else for network data fetch


        gson = new Gson();

       return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        player.release();
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
        Constants.current_lesson_id = position;
        MediaItem mediaItem = MediaItem.fromUri(lessonsViewModel.getMyLessons().getValue().get(position).getVideo_url_web());
// Set the media item to be played.
        player.setMediaItem(mediaItem);
// Prepare the player.
        player.prepare();
// Start the playback.
        player.play();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(getContext()).build();
        // Bind the player to the view.
        binding.exoplayerView.setPlayer(player);
        // Build the media item.
        MediaItem mediaItem = MediaItem.fromUri(lessonsViewModel.getMyLessons().getValue().get(Constants.current_lesson_id).getVideo_url_web());
// Set the media item to be played.
        player.setMediaItem(mediaItem);
// Prepare the player.
        player.prepare();
// Start the playback.
        player.play();
    }
}