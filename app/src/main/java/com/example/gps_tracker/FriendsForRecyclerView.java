package com.example.gps_tracker;

import android.app.Activity;
import android.net.Uri;

public class FriendsForRecyclerView {

    private String name;
    private String email;
    private String photo;
    private Activity activity;
    private Double lng;
    private Double lat;

    public FriendsForRecyclerView(String name, String email, String photo,Activity activity,Double lng,Double lat){

        this.name=name;
        this.email = email;
        this.photo = photo;
        this.activity = activity;
        this.lat = lat;
        this.lng = lng;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
