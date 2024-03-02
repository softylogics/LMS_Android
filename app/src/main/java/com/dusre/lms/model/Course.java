package com.dusre.lms.model;

public class Course {
    private String title;
    private float rating;
    private String progressPercentage;
    private int progress;
    private String completedLectures;

    public Course(String title, float rating, String progressPercentage, int progress, String completedLectures) {
        this.title = title;
        this.rating = rating;
        this.progressPercentage = progressPercentage;
        this.progress = progress;
        this.completedLectures = completedLectures;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(String progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getCompletedLectures() {
        return completedLectures;
    }

    public void setCompletedLectures(String completedLectures) {
        this.completedLectures = completedLectures;
    }
}
