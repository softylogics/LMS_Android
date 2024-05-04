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
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Lesson;

import java.util.List;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.CourseViewHolder> {

    private Context context;



    private final SetOnClickListener listener;

    private List<Lesson> lessons;

    public LessonsAdapter(Context context, List<Lesson> lessons,SetOnClickListener listener) {

        this.context = context;
        this.lessons = lessons;

        this.listener = listener;

    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CourseDetailNestedItemViewBinding binding = CourseDetailNestedItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Lesson lesson = this.lessons.get(position);

        holder.bind(lesson);
        holder.binding.cbCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onCheckBoxClick(position);

            }
        });

        holder.binding.ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDownloadButtonClick(position);
            }
        });

        holder.binding.lessonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.current_lesson_id = position;
                listener.onLessonNameClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private CourseDetailNestedItemViewBinding binding;

        public CourseViewHolder(@NonNull CourseDetailNestedItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Lesson lesson) {
            binding.txtLessonTitle.setText(lesson.getTitle());
            binding.txtNestedLectureLength.setText(lesson.getDuration() + " Min");
            if(lesson.is_downloaded || lesson.getVideo_url_web().contains("youtube")) {
                binding.ivDownload.setAlpha(0f);
                binding.ivDownload.setEnabled(false);
            }
//            if(lesson.getIs_completed()) {
//                binding.cbCompleted.setChecked(true);
//            }
        }
    }
}
