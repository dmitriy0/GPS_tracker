package com.example.gps_tracker;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<FriendsForRecyclerView> friendsForRecyclerViews;

    DataAdapter(Context context, List<FriendsForRecyclerView> friendsForRecyclerViews) {
        this.friendsForRecyclerViews = friendsForRecyclerViews;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.friend_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        FriendsForRecyclerView friendsForRecyclerView = friendsForRecyclerViews.get(position);
        holder.imageView.setImageResource(friendsForRecyclerView.getImage());
        holder.nameView.setText(friendsForRecyclerView.getName());
        holder.companyView.setText(friendsForRecyclerView.getCompany());
    }

    @Override
    public int getItemCount() {
        return friendsForRecyclerViews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView nameView, companyView;
        ViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            nameView = (TextView) view.findViewById(R.id.name);
            companyView = (TextView) view.findViewById(R.id.company);
        }
    }
}
