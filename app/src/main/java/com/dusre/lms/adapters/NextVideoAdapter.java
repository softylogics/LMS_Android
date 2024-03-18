package com.dusre.lms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;

import com.dusre.lms.databinding.NextVideosItemViewBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Lesson;
import com.dusre.lms.viewmodel.LessonsViewModel;


import java.util.ArrayList;
import java.util.List;

public class NextVideoAdapter extends RecyclerView.Adapter<NextVideoAdapter.CourseViewHolder> {

    private Context context;
    private List<Lesson> lessonList;
    private LessonsViewModel lessonsViewModel;

    private final SetOnClickListener listener;

    private List<Lesson> lessons;
    private int expandedPosition = -1;


    public NextVideoAdapter(Context context, LessonsViewModel lessonsViewModel, SetOnClickListener listener) {
        this.lessonsViewModel = lessonsViewModel;
        this.context = context;
        lessonList = new ArrayList<>();
        lessons = new ArrayList<>();
        this.listener = listener;
        lessonsViewModel.getMyLessons().observeForever(lesson -> {
            lessonList.clear();
            if (lesson != null) {
                lessonList.addAll(lesson);
            }
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NextVideosItemViewBinding binding = NextVideosItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {


        Lesson lesson = this.lessonsViewModel.getMyLessons().getValue().get(position);

        holder.bind(lesson);
        holder.binding.lessonLinearLayout.setOnClickListener(v->{
                listener.onNextLessonClick(position);

        });


    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private NextVideosItemViewBinding binding;

        public CourseViewHolder(@NonNull NextVideosItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Lesson lesson) {
           binding.txtNextLessonTitle.setText(lesson.getTitle());
           binding.txtNextLectureLength.setText(lesson.getDuration() + " min");


        }
    }
}
