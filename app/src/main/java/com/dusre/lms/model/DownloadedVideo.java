package com.dusre.lms.model;

public class DownloadedVideo {

    private String id;
    private String title;
    private String duration;
    private String course_id;
    private String course_title;
    private String section_id;
    private String section_title;
    private String video_file_path;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getSection_title() {
        return section_title;
    }

    public void setSection_title(String section_title) {
        this.section_title = section_title;
    }


    public String getVideo_file_path() {
        return video_file_path;
    }

    public void setVideo_file_path(String videoFilePath) {
        this.video_file_path = videoFilePath;
    }
}
