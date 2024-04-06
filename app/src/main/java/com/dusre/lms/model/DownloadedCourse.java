package com.dusre.lms.model;

import java.util.List;


//This class is used to store the downloaded videos in the form of course where each course has sections and each section has lessons.
//DownloadedSection and DownloadedLesson classes are used as sections and lessons.
public class DownloadedCourse {

    private String courseID;
    private String courseTitle;
    private List<DownloadedSection> downloadedSections;



    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public List<DownloadedSection> getDownloadedSections() {
        return downloadedSections;
    }

    public void setDownloadedSections(List<DownloadedSection> downloadedSections) {
        this.downloadedSections = downloadedSections;
    }
}
