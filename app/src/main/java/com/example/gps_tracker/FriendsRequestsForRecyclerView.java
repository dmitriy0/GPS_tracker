package com.example.gps_tracker;

import android.app.Activity;
import android.content.Context;

public class FriendsRequestsForRecyclerView {

    private String email;
    private String confirm;
    private Context context;
    private Activity activity;

    public FriendsRequestsForRecyclerView(String email, String confirm, Context context, Activity activity){

        this.email=email;
        this.confirm = confirm;
        this.context = context;
        this.activity = activity;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}