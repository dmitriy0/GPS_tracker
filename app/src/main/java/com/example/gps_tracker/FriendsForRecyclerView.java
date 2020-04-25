package com.example.gps_tracker;

public class FriendsForRecyclerView {

    private String name;
    private String company;
    private int image;

    public FriendsForRecyclerView(String name, String company, int image){

        this.name=name;
        this.company = company;
        this.image = image;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    int getImage() {
        return this.image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
