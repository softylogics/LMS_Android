package com.dusre.lms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dusre.lms.databinding.CourseItemViewBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Course;
import com.dusre.lms.viewmodel.CoursesViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyCoursesAdapter extends RecyclerView.Adapter<MyCoursesAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;
    private CoursesViewModel coursesViewModel;
    private final SetOnClickListener listener;

    public MyCoursesAdapter(Context context, List<Course> courseList, CoursesViewModel coursesViewModel, SetOnClickListener listener) {
        this.coursesViewModel = coursesViewModel;
        this.context = context;
        this.courseList = courseList;
        this.listener = listener;
        coursesViewModel.getMyCourses().observeForever(course -> {
            courseList.clear();
            if (course != null) {
                courseList.addAll(course);
            }
            notifyDataSetChanged();
        });
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickCourse(position);
            }
        });
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
            Picasso.get().load(course.getThumbnail()).into(binding.imageCourse);

            binding.txtProgressPercentage.setText(course.getCompletion() + "% completed");
            binding.progressBar.setProgress(calculateProgress(course));
            binding.txtCompletedLectures.setText(course.getTotal_number_of_completed_lessons()+"/"+course.getTotal_number_of_lessons());


        }
    }
    public int calculateProgress(int completedLectures, int totalLectures) {
        if (totalLectures == 0) {
            return 0; // Avoid division by zero
        }

        double progressPercentage = (double) completedLectures / totalLectures * 100;
        return (int) progressPercentage;
    }
    private int calculateProgress(Course course) {
        double m = (double) course.getTotal_number_of_completed_lessons()/course.getTotal_number_of_lessons() *100;

        return (int) m;
    }
}
