package com.dusre.lms.model;

public class Lesson{
    public String id;
    public String title;
    public String duration;
    public String course_id;
    public String section_id;
    public String video_type;
    public String video_url;
    public String video_url_web;
    public String video_type_web;
    public String lesson_type;
    public String is_free;
    public String attachment;
    public String attachment_url;
    public String attachment_type;
    public String summary;
    public int is_completed;
    public boolean user_validity;

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

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_url_web() {
        return video_url_web;
    }

    public void setVideo_url_web(String video_url_web) {
        this.video_url_web = video_url_web;
    }

    public String getVideo_type_web() {
        return video_type_web;
    }

    public void setVideo_type_web(String video_type_web) {
        this.video_type_web = video_type_web;
    }

    public String getLesson_type() {
        return lesson_type;
    }

    public void setLesson_type(String lesson_type) {
        this.lesson_type = lesson_type;
    }

    public String getIs_free() {
        return is_free;
    }

    public void setIs_free(String is_free) {
        this.is_free = is_free;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachment_url() {
        return attachment_url;
    }

    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }

    public String getAttachment_type() {
        return attachment_type;
    }

    public void setAttachment_type(String attachment_type) {
        this.attachment_type = attachment_type;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getIs_completed() {
        return is_completed;
    }

    public void setIs_completed(int is_completed) {
        this.is_completed = is_completed;
    }

    public boolean isUser_validity() {
        return user_validity;
    }

    public void setUser_validity(boolean user_validity) {
        this.user_validity = user_validity;
    }
}
