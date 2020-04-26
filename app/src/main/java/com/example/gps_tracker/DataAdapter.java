package com.example.gps_tracker;

import android.content.Context;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<FriendsForRecyclerView> friendsForRecyclerViews;

    private StorageReference mStorageRef;

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
    public void onBindViewHolder(final DataAdapter.ViewHolder holder, int position) {
        final FriendsForRecyclerView friendsForRecyclerView = friendsForRecyclerViews.get(position);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        holder.emailView.setText(friendsForRecyclerView.getEmail());
        holder.nameView.setText(friendsForRecyclerView.getName());

        StorageReference riversRef = mStorageRef.child(friendsForRecyclerView.getPhoto());

        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).into(holder.imageView);
            }
        });



    }

    @Override
    public int getItemCount() {
        return friendsForRecyclerViews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView emailView, nameView;
        ViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            emailView = (TextView) view.findViewById(R.id.email);
            nameView = (TextView) view.findViewById(R.id.name);
        }
    }
}
