package com.example.gps_tracker;

import android.app.Activity;
import android.content.Context;

public class FriendsRequestsForRecyclerView {
    //ещё один класс который нужен для recyclerView запросов
    private String email;
    private Context context;
    private Activity activity;

    public FriendsRequestsForRecyclerView(String email, Context context, Activity activity){

        this.email=email;
        this.context = context;
        this.activity = activity;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
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
