package com.dusre.lms.model;

import java.util.ArrayList;
import java.util.List;

public class CourseForPostDownloadService {
        public String id;
        public String title;
        public String short_description;
        public String description;
        public ArrayList<Object> outcomes;
        public String faqs;
        public String language;
        public String category_id;
        public String sub_category_id;
        public String section;
        public ArrayList<Object> requirements;
        public String price;
        public Object discount_flag;
        public String discounted_price;
        public String level;
        public String user_id;
        public String thumbnail;
        public String video_url;
        public String date_added;
        public String last_modified;
        public String course_type;
        public String is_top_course;
        public String is_admin;
        public String status;
        public String course_overview_provider;
        public String meta_keywords;
        public String meta_description;
        public Object is_free_course;
        public String multi_instructor;
        public String enable_drip_content;
        public String creator;
        public Object expiry_period;
        public int rating;
        public int number_of_ratings;
        public String instructor_name;
        public String instructor_image;
        public int total_enrollment;
        public String shareable_link;

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> sections;
        public int completion;
        public int total_number_of_lessons;
        public int total_number_of_completed_lessons;

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

        public String getShort_description() {
            return short_description;
        }

        public void setShort_description(String short_description) {
            this.short_description = short_description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ArrayList<Object> getOutcomes() {
            return outcomes;
        }

        public void setOutcomes(ArrayList<Object> outcomes) {
            this.outcomes = outcomes;
        }

        public String getFaqs() {
            return faqs;
        }

        public void setFaqs(String faqs) {
            this.faqs = faqs;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getSub_category_id() {
            return sub_category_id;
        }

        public void setSub_category_id(String sub_category_id) {
            this.sub_category_id = sub_category_id;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public ArrayList<Object> getRequirements() {
            return requirements;
        }

        public void setRequirements(ArrayList<Object> requirements) {
            this.requirements = requirements;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public Object getDiscount_flag() {
            return discount_flag;
        }

        public void setDiscount_flag(Object discount_flag) {
            this.discount_flag = discount_flag;
        }

        public String getDiscounted_price() {
            return discounted_price;
        }

        public void setDiscounted_price(String discounted_price) {
            this.discounted_price = discounted_price;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        public String getDate_added() {
            return date_added;
        }

        public void setDate_added(String date_added) {
            this.date_added = date_added;
        }

        public String getLast_modified() {
            return last_modified;
        }

        public void setLast_modified(String last_modified) {
            this.last_modified = last_modified;
        }

        public String getCourse_type() {
            return course_type;
        }

        public void setCourse_type(String course_type) {
            this.course_type = course_type;
        }

        public String getIs_top_course() {
            return is_top_course;
        }

        public void setIs_top_course(String is_top_course) {
            this.is_top_course = is_top_course;
        }

        public String getIs_admin() {
            return is_admin;
        }

        public void setIs_admin(String is_admin) {
            this.is_admin = is_admin;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCourse_overview_provider() {
            return course_overview_provider;
        }

        public void setCourse_overview_provider(String course_overview_provider) {
            this.course_overview_provider = course_overview_provider;
        }

        public String getMeta_keywords() {
            return meta_keywords;
        }

        public void setMeta_keywords(String meta_keywords) {
            this.meta_keywords = meta_keywords;
        }

        public String getMeta_description() {
            return meta_description;
        }

        public void setMeta_description(String meta_description) {
            this.meta_description = meta_description;
        }

        public Object getIs_free_course() {
            return is_free_course;
        }

        public void setIs_free_course(Object is_free_course) {
            this.is_free_course = is_free_course;
        }

        public String getMulti_instructor() {
            return multi_instructor;
        }

        public void setMulti_instructor(String multi_instructor) {
            this.multi_instructor = multi_instructor;
        }

        public String getEnable_drip_content() {
            return enable_drip_content;
        }

        public void setEnable_drip_content(String enable_drip_content) {
            this.enable_drip_content = enable_drip_content;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public Object getExpiry_period() {
            return expiry_period;
        }

        public void setExpiry_period(Object expiry_period) {
            this.expiry_period = expiry_period;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public int getNumber_of_ratings() {
            return number_of_ratings;
        }

        public void setNumber_of_ratings(int number_of_ratings) {
            this.number_of_ratings = number_of_ratings;
        }

        public String getInstructor_name() {
            return instructor_name;
        }

        public void setInstructor_name(String instructor_name) {
            this.instructor_name = instructor_name;
        }

        public String getInstructor_image() {
            return instructor_image;
        }

        public void setInstructor_image(String instructor_image) {
            this.instructor_image = instructor_image;
        }

        public int getTotal_enrollment() {
            return total_enrollment;
        }

        public void setTotal_enrollment(int total_enrollment) {
            this.total_enrollment = total_enrollment;
        }

        public String getShareable_link() {
            return shareable_link;
        }

        public void setShareable_link(String shareable_link) {
            this.shareable_link = shareable_link;
        }

        public int getCompletion() {
            return completion;
        }

        public void setCompletion(int completion) {
            this.completion = completion;
        }

        public int getTotal_number_of_lessons() {
            return total_number_of_lessons;
        }

        public void setTotal_number_of_lessons(int total_number_of_lessons) {
            this.total_number_of_lessons = total_number_of_lessons;
        }

        public int getTotal_number_of_completed_lessons() {
            return total_number_of_completed_lessons;
        }

        public void setTotal_number_of_completed_lessons(int total_number_of_completed_lessons) {
            this.total_number_of_completed_lessons = total_number_of_completed_lessons;
        }

}
