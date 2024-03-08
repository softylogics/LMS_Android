package com.dusre.lms.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dusre.lms.model.Course;

import java.util.List;

public class CoursesViewModel extends ViewModel {
    public MutableLiveData<List<Course>> myCourses;

    public CoursesViewModel() {
        this.myCourses = new MutableLiveData<>();
    }

    public MutableLiveData<List<Course>> getMyCourses() {
        return myCourses;
    }

    public void setMyCourses(List<Course> myCourses) {
        this.myCourses.setValue(myCourses);
    }
}
