package com.dusre.lms.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dusre.lms.R;
import com.dusre.lms.Util.Constants;
import com.dusre.lms.databinding.DownloadedCourseDetailItemViewBinding;
import com.dusre.lms.listeners.SetOnClickListener;
import com.dusre.lms.model.DownloadedLesson;
import com.dusre.lms.model.DownloadedSection;
import com.dusre.lms.viewmodel.DownloadedVideoViewModel;

import java.util.ArrayList;
import java.util.List;

public class DownloadedSectionsAdapter extends RecyclerView.Adapter<DownloadedSectionsAdapter.CourseViewHolder> {


    private final List<DownloadedSection> downlaodedSectionList;
    private Context context;

    private DownloadedVideoViewModel downloadedVideoViewModel;

    private final SetOnClickListener listener;

    private List<DownloadedLesson> lessons;



    public DownloadedSectionsAdapter(Context context, DownloadedVideoViewModel downloadedVideoViewModel, SetOnClickListener listener) {
        this.downloadedVideoViewModel = downloadedVideoViewModel;
        this.context = context;
        downlaodedSectionList = new ArrayList<>();
        lessons = new ArrayList<>();
        this.listener = listener;
        downlaodedSectionList.addAll(downloadedVideoViewModel.getDownloadedCourses().getValue().get(Constants.current_downloaded_course_id).getDownloadedSections());
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadedCourseDetailItemViewBinding binding = DownloadedCourseDetailItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        DownloadedSection section = this.downlaodedSectionList.get(position);
        holder.bind(section);
        boolean isExpandable = section.isIs_expandable();
        holder.binding.downloadedExpandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        if (isExpandable){
            holder.binding.downloadedArroImageview.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        }else{
            holder.binding.downloadedArroImageview.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        }
        DownloadedLessonsAdapter adapter = new DownloadedLessonsAdapter(context, lessons, listener);
        holder.binding.downloadedChildRv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.binding.downloadedChildRv.setHasFixedSize(true);
        holder.binding.downloadedChildRv.setAdapter(adapter);
        holder.binding.downloadedLinearLayout.setOnClickListener(v -> {
            if(section.is_expandable){
                section.setIs_expandable(false);
            }
            else{
                section.setIs_expandable(true);
                lessons = section.getDownloadedLessons();
                Constants.current_downloaded_section_id = position;
                changeStateOfItemsInLayout(downlaodedSectionList.get(holder.getLayoutPosition()),holder.getLayoutPosition());
            }


            notifyDataSetChanged();
        });
    }
    private void changeStateOfItemsInLayout(DownloadedSection section, int p) {
        for (int i = 0; i < downlaodedSectionList.size(); i++) {
            if (i == p) {
                section.setIs_expandable(true);
                //Since this is the tapped item, we will skip
                //the rest of loop for this item and set it expanded
                continue;
            }
            downlaodedSectionList.get(i).setIs_expandable(false);
        }
    }
    @Override
    public int getItemCount() {
        Log.d("fragmenet" , "downloadSectionAdapterItemCount");
        Log.d("fragmenet" , "downloadSectionAdapterItemCount" + downlaodedSectionList);
        Log.d("fragmenet" , "downloadSectionAdapterItemCount" + lessons);
        return downlaodedSectionList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        private DownloadedCourseDetailItemViewBinding binding;

        public CourseViewHolder(@NonNull DownloadedCourseDetailItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DownloadedSection section) {
            binding.txtDownloadedModuleTitle.setText(section.getSectionTitle());


        }
    }


}
