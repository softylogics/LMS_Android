package com.dusre.lms.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.dusre.lms.model.Section;

import java.util.List;

public class SectionsViewModel extends ViewModel {
    public MutableLiveData<List<Section>> sections;

    public SectionsViewModel() {
        this.sections = new MutableLiveData<>();
    }

    public MutableLiveData<List<Section>> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections.setValue(sections);
    }
}
