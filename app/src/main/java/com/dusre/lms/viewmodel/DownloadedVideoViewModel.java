package com.dusre.lms.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dusre.lms.model.DownloadedCourse;

import java.util.List;

public class DownloadedVideoViewModel extends ViewModel {

    public MutableLiveData<List<DownloadedCourse>> downloadedCourses;

    public DownloadedVideoViewModel() {
        this.downloadedCourses = new MutableLiveData<>();
    }

    public MutableLiveData<List<DownloadedCourse>> getDownloadedCourses() {
        return downloadedCourses;
    }

    public void setDownloadedCourses(List<DownloadedCourse> downloadedCourses) {
        this.downloadedCourses.setValue(downloadedCourses);
    }
}
