package com.example.attendanceapp;

public class RecordModel {

    private String courseCode, type, record_date;
    private Boolean record_status;

    public RecordModel(){
    }

    public RecordModel(String courseCode, String type, String record_date, Boolean record_status){
        this.courseCode = courseCode;
        this.type = type;
        this.record_date = record_date;
        this.record_status = record_status;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecord_Date() {
        return record_date;
    }

    public void setRecord_Date(String date) {
        this.record_date = date;
    }

    public Boolean getRecord_Status() {
        return record_status;
    }

    public void setRecord_Status(Boolean status) {
        this.record_status = status;
    }
}
