package com.dusre.lms.listeners;

import android.view.View;

public interface SetOnClickListener {

    void onItemClickCourseSection(int postion);

    void onCheckBoxClick(int position);

    void onDownloadButtonClick(int position);
    void onItemClickCourse( int position);

    void onLessonNameClick(int position);

    void onNextLessonClick(int position);

    void onDownloadedItemClickCourse( int position);

    void onDownloadedLessonNameClick(int position);

    void onDownloadedNextLessonClick(int position);

    void onDownloadDeleteVideo(int position);
}
