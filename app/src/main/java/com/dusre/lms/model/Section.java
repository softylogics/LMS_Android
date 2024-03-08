package com.dusre.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Section{
    public String id;
    public String title;
    public String course_id;
    public long start_date;
    public long end_date;
    public String restricted_by;
    public String order;
    public List<Lesson> lessons;
    public String total_duration;
    public int lesson_counter_starts;
    public int lesson_counter_ends;
    public int completed_lesson_number;
    public boolean user_validity;
    public boolean is_expandable;

    public boolean isExpandable() {
        return is_expandable;
    }

    public void setIs_expandable(boolean is_expandable) {
        this.is_expandable = is_expandable;
    }

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

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public long getStart_date() {
        return start_date;
    }

    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }

    public long getEnd_date() {
        return end_date;
    }

    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    public String getRestricted_by() {
        return restricted_by;
    }

    public void setRestricted_by(String restricted_by) {
        this.restricted_by = restricted_by;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public String getTotal_duration() {
        return total_duration;
    }

    public void setTotal_duration(String total_duration) {
        this.total_duration = total_duration;
    }

    public int getLesson_counter_starts() {
        return lesson_counter_starts;
    }

    public void setLesson_counter_starts(int lesson_counter_starts) {
        this.lesson_counter_starts = lesson_counter_starts;
    }

    public int getLesson_counter_ends() {
        return lesson_counter_ends;
    }

    public void setLesson_counter_ends(int lesson_counter_ends) {
        this.lesson_counter_ends = lesson_counter_ends;
    }

    public int getCompleted_lesson_number() {
        return completed_lesson_number;
    }

    public void setCompleted_lesson_number(int completed_lesson_number) {
        this.completed_lesson_number = completed_lesson_number;
    }

    public boolean isUser_validity() {
        return user_validity;
    }

    public void setUser_validity(boolean user_validity) {
        this.user_validity = user_validity;
    }
}

