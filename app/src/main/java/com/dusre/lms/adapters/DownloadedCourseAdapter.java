package com.dusre.lms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dusre.lms.databinding.DownloadedCourseItemViewBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.DownloadedCourse;
import com.dusre.lms.model.DownloadedSection;
import com.dusre.lms.model.DownloadedVideo;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DownloadedCourseAdapter  extends RecyclerView.Adapter<DownloadedCourseAdapter.CourseViewHolder> {


    private Context context;
    private List<DownloadedCourse> downloadedVideoList;
    private DownloadedVideoViewModel downloadedVideoViewModel;
    private final SetOnClickListener listener;

    public DownloadedCourseAdapter(Context context, DownloadedVideoViewModel downloadedVideoViewModel, SetOnClickListener listener) {
        this.downloadedVideoViewModel = downloadedVideoViewModel;
        this.context = context;
        downloadedVideoList = new ArrayList<>();
        this.listener = listener;
        downloadedVideoViewModel.getDownloadedCourses().observeForever(downloadedVideos -> {
            downloadedVideoList.clear();
            if (downloadedVideos != null) {
                downloadedVideoList.addAll(downloadedVideos);
            }
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public DownloadedCourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadedCourseItemViewBinding binding = DownloadedCourseItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadedCourseAdapter.CourseViewHolder holder, int position) {
        DownloadedCourse downloadedCourse = downloadedVideoList.get(position);
        holder.bind(downloadedCourse);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDownloadedItemClickCourse(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadedVideoList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private DownloadedCourseItemViewBinding binding;

        public CourseViewHolder(@NonNull DownloadedCourseItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DownloadedCourse downloadedCourse) {
            binding.txtDownloadedCourseTitleItem.setText(downloadedCourse.getCourseTitle());

            binding.txtDownlaodedTotalLesson.setText("Total Lessons: " + getTotalLessons(downloadedCourse));
            Picasso.get().load("https://virus.sahlaapps.xyz/uploads/thumbnails/course_thumbnails/optimized/course_thumbnail_default-new_11703241630.jpg").into(binding.imageCourse);


        }

        private int getTotalLessons(DownloadedCourse downloadedCourse) {
            int totalLessons = 0;
            for(DownloadedSection section: downloadedCourse.getDownloadedSections()){
                totalLessons += section.getDownloadedLessons().size();
            }
            return totalLessons;
        }
    }

}
