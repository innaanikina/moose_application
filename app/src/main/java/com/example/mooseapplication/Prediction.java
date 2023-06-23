package com.example.mooseapplication;

import androidx.annotation.NonNull;

import java.util.Date;

public class Prediction {
    private int camera_id;
    private Date prediction_time;
    private int moose_count;
    private int bear_count;
    private int hog_count;
    private int lynx_count;
    private String photo;

    public Prediction(int camera_id, Date prediction_time, int moose_count, int bear_count,
                      int hog_count, int lynx_count, String photo) {
        this.camera_id = camera_id;
        this.prediction_time = prediction_time;
        this.moose_count = moose_count;
        this.bear_count = bear_count;
        this.hog_count = hog_count;
        this.lynx_count = lynx_count;
        this.photo = photo;
    }

    public int getCamera_id() {
        return camera_id;
    }

    public Date getPrediction_time() {
        return prediction_time;
    }

    public int getMoose_count() {
        return moose_count;
    }

    public int getBear_count() {
        return bear_count;
    }

    public int getHog_count() {
        return hog_count;
    }

    public int getLynx_count() {
        return hog_count;
    }

    public String getPhoto() {
        return photo;
    }

    @NonNull
    @Override
    public String toString() {
        return "Camera: " + camera_id + ", prediction time: " + prediction_time.toString()
                + ", deers: " + moose_count + ", bears: " + bear_count + ", hogs: " + hog_count
                + ", lynxs: " + lynx_count;
    }
}
