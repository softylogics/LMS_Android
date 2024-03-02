package com.dusre.lms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dusre.lms.databinding.CourseItemViewBinding;
import com.dusre.lms.model.Course;

import java.util.List;

public class MyCoursesAdapter extends RecyclerView.Adapter<MyCoursesAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;

    public MyCoursesAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CourseItemViewBinding binding = CourseItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private CourseItemViewBinding binding;

        public CourseViewHolder(@NonNull CourseItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Course course) {
            binding.txtCourseTitleItem.setText(course.getTitle());
            binding.ratingBar.setRating(course.getRating());
            binding.txtRating.setText(String.valueOf(course.getRating()));
            binding.txtProgressPercentage.setText(course.getProgressPercentage());
            binding.progressBar.setProgress(course.getProgress());
            binding.txtCompletedLectures.setText(course.getCompletedLectures());

            // Set other course details as needed

            // You can load course image using Glide or any other image loading library
            // Glide.with(context).load(course.getImageUrl()).into(binding.imageCourse);
        }
    }
}
