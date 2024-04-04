package com.dusre.lms.Util;

import com.dusre.lms.model.Lesson;

import java.util.List;

public class Constants {

    public static final String LOGGED_IN = "loggesIn";
    public static final String TOKEN = "token";
    public static final String USERNAME = "username";
    public static String url = "https://virus.sahlaapps.xyz/api/";
    public static int current_course_id = 0;
    public static int current_section_id = 0;
    public static int current_lesson_id = 0;
    public static int current_downloaded_course_id = 0;
    public static int current_downloaded_lesson_id = 0;
    public static int current_downloaded_section_id = 0;
    public static boolean isDownloadVideoPlay = false;
    public static List<Lesson> lessons = null;
}
