package com.dusre.lms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dusre.lms.Util.Constants;
import com.dusre.lms.databinding.CourseDetailNestedItemViewBinding;
import com.dusre.lms.databinding.DownloadedCourseDetailNestedItemViewBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.DownloadedLesson;
import com.dusre.lms.model.Lesson;

import java.util.List;

public class DownloadedLessonsAdapter extends RecyclerView.Adapter<DownloadedLessonsAdapter.CourseViewHolder> {
    private Context context;



    private final SetOnClickListener listener;

    private List<DownloadedLesson> lessons;

    public DownloadedLessonsAdapter(Context context, List<DownloadedLesson> lessons, SetOnClickListener listener) {

        this.context = context;
        this.lessons = lessons;

        this.listener = listener;

    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadedCourseDetailNestedItemViewBinding binding = DownloadedCourseDetailNestedItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        DownloadedLesson lesson = this.lessons.get(position);

        holder.bind(lesson, position);

        holder.binding.downloadedNestedDel.setOnClickListener(v->{
            listener.onDownloadDeleteVideo(position);
        });
        holder.binding.txtDownloadedNestedLessonTitle.setOnClickListener(v->{
            listener.onDownloadedLessonNameClick(position);
        });

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private DownloadedCourseDetailNestedItemViewBinding binding;

        public CourseViewHolder(@NonNull DownloadedCourseDetailNestedItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DownloadedLesson lesson, int position) {
            binding.txtDownloadedNestedLessonTitle.setText(lesson.getLessonTitle());

            binding.txtLessonNum.setText((position+1)+"");

        }
    }
}
