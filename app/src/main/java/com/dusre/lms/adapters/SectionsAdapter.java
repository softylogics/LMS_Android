package com.dusre.lms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dusre.lms.R;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.databinding.CourseDetailItemViewBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.Lesson;
import com.dusre.lms.model.Section;
import com.dusre.lms.viewmodel.SectionsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.CourseViewHolder> {

    private Context context;
    private List<Section> sectionList;
    private SectionsViewModel sectionsViewModel;

    private final SetOnClickListener listener;

    private List<Lesson> lessons;
    private LessonsAdapter adapter;


    public SectionsAdapter(Context context, SectionsViewModel sectionsViewModel, SetOnClickListener listener) {
        this.sectionsViewModel = sectionsViewModel;
        this.context = context;
        sectionList = new ArrayList<>();
        lessons = new ArrayList<>();
        this.listener = listener;
        sectionsViewModel.getSections().observeForever(section -> {
            sectionList.clear();
            if (section != null) {
                sectionList.addAll(section);
            }
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CourseDetailItemViewBinding binding = CourseDetailItemViewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Section section = this.sectionsViewModel.getSections().getValue().get(position);
        holder.bind(section);
        boolean isExpandable = section.isExpandable();
        holder.binding.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        if (isExpandable){
            holder.binding.arrowImageview.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);

        }else{
            holder.binding.arrowImageview.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        }
        adapter = new LessonsAdapter(context, lessons, listener);
        holder.binding.childRv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.binding.childRv.setHasFixedSize(true);
        holder.binding.childRv.setAdapter(adapter);
        holder.binding.linearLayoutCourseDetailItem.setOnClickListener(v -> {
            if(section.isExpandable()){
                section.setIs_expandable(false);
            }
            else{
                section.setIs_expandable(true);
                changeStateOfItemsInLayout(sectionList.get(holder.getLayoutPosition()),holder.getLayoutPosition());
                lessons = section.getLessons();
                Constants.current_section_id = position;
            }


            notifyDataSetChanged();
        });
    }
    private void changeStateOfItemsInLayout(Section section, int position) {
        for (int i = 0; i < sectionList.size(); i++) {
            if (i == position) {
                section.setIs_expandable(true);
                //Since this is the tapped item, we will skip
                //the rest of loop for this item and set it expanded
                continue;
            }
            sectionList.get(i).setIs_expandable(false);
        }
    }
    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public void reload() {
        adapter.notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private CourseDetailItemViewBinding binding;

        public CourseViewHolder(@NonNull CourseDetailItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Section section) {
            binding.txtModuleTitle.setText(section.getTitle());
            binding.btnTotalLessons.setText(section.getLessons().size() + " Lessons");
            binding.btnLessonTiming.setText(section.getTotal_duration());


        }
    }
}
