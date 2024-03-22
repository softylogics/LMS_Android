package com.dusre.lms.model;

import java.util.List;

public class DownloadedSection {
    private String sectionID;
    private String sectionTitle;
    private List<DownloadedLesson> downloadedLessons;
    public boolean is_expandable;

    public boolean isIs_expandable() {
        return is_expandable;
    }

    public void setIs_expandable(boolean is_expandable) {
        this.is_expandable = is_expandable;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public List<DownloadedLesson> getDownloadedLessons() {
        return downloadedLessons;
    }

    public void setDownloadedLessons(List<DownloadedLesson> downloadedLessons) {
        this.downloadedLessons = downloadedLessons;
    }
}