package com.dusre.lms.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
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

import com.dusre.lms.model.DownloadedCourse;
import com.dusre.lms.model.Lesson;
import com.dusre.lms.model.Section;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;
import com.dusre.lms.viewmodel.LessonsViewModel;
import com.dusre.lms.viewmodel.SectionsViewModel;
import com.dusre.lms.viewmodel.CoursesViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
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

    private DownloadedVideoViewModel downloadedVideoViewModel;
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
        downloadedVideoViewModel = new ViewModelProvider(requireActivity()).get(DownloadedVideoViewModel.class);
        initializePlayer();
        binding.recyclerViewNextVideos.setLayoutManager(new LinearLayoutManager(getContext()));
       if(!Constants.isDownloadVideoPlay) {
           adapter = new NextVideoAdapter(getContext(), lessonsViewModel, this);
           binding.recyclerViewNextVideos.setAdapter(adapter);
       }

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

    private void initializePlayer() {
        if(Constants.isDownloadVideoPlay){
            player = new ExoPlayer.Builder(getContext()).build();
            // Bind the player to the view.
            binding.exoplayerView.setPlayer(player);
            // Build the media item.
            String folderName = "DownloadedVideos";
            List<DownloadedCourse> videos = downloadedVideoViewModel.getDownloadedCourses().getValue();

            String path = videos.get(Constants.current_downloaded_course_id).getDownloadedSections().get(Constants.current_downloaded_section_id).getDownloadedLessons().get(Constants.current_downloaded_lesson_id).getVideoPath();
            String name = path.substring(path.lastIndexOf("/")+1);
            //            File file = new File(getActivity().getFilesDir(), name);
            File directory = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

// Create a File object representing the downloaded file
            File downloadedFile = new File(directory, name);
//            String uri = downloadedFile.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(getContext(), "com.dusre.lms.fileprovider", downloadedFile);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(path));
// Set the media item to be played.
            player.setMediaItem(mediaItem);
// Prepare the player.
            player.prepare();
// Start the playback.
            player.play();
        }
        else {
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
}