package com.example.gps_tracker;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    //класс который нужен для recyclerView друзей
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

        holder.emailView.setText(friendsForRecyclerView.getEmail());
        holder.nameView.setText(friendsForRecyclerView.getName());

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(friendsForRecyclerView.getPhoto());

        //отрисовка аватарки друга
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(holder.imageView);
            }
        });

        //обработчик нажатия на друга
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(friendsForRecyclerView.getActivity(), MainActivity.class);
                intent.putExtra("options","map");
                intent.putExtra("lat",friendsForRecyclerView.getLat());
                intent.putExtra("lng",friendsForRecyclerView.getLng());
                startActivity(friendsForRecyclerView.getActivity(),intent,null);
            }
        });



    }

    @Override
    public int getItemCount() {
        return friendsForRecyclerViews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final LinearLayout linearLayout;
        final TextView emailView, nameView;
        ViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            emailView = (TextView) view.findViewById(R.id.email);
            nameView = (TextView) view.findViewById(R.id.name);
            linearLayout = (LinearLayout) view.findViewById(R.id.layout);
        }
    }
}
