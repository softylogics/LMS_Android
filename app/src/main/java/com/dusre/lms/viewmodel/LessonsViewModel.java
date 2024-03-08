package com.dusre.lms.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dusre.lms.model.Course;
import com.dusre.lms.model.Lesson;

import java.util.List;

public class LessonsViewModel extends ViewModel {
    public MutableLiveData<List<Lesson>> myLessons;

    public LessonsViewModel() {
        this.myLessons = new MutableLiveData<>();
    }

    public MutableLiveData<List<Lesson>> getMyLessons() {
        return myLessons;
    }

    public void setMyLessons(List<Lesson> myLessons) {
        this.myLessons.setValue(myLessons);
    }
}
