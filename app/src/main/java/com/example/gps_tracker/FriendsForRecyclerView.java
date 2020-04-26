package com.example.gps_tracker;

import android.net.Uri;

public class FriendsForRecyclerView {

    private String name;
    private String email;
    private String photo;

    public FriendsForRecyclerView(String name, String email, String photo){

        this.name=name;
        this.email = email;
        this.photo = photo;
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
}
