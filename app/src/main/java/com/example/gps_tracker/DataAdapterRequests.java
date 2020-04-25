package com.example.gps_tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static androidx.core.content.ContextCompat.startActivity;

class DataAdapterRequests extends RecyclerView.Adapter<DataAdapterRequests.ViewHolder> {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    private SharedPreferences mSettings;
    private LayoutInflater inflater;
    private List<FriendsRequestsForRecyclerView> friendsRequestsForRecyclerViews;
    private String email;
    private int count;
    private int counterFor;



    DataAdapterRequests(Context context, List<FriendsRequestsForRecyclerView> friendsRequestsForRecyclerViews) {
        this.friendsRequestsForRecyclerViews = friendsRequestsForRecyclerViews;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public DataAdapterRequests.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.requests_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataAdapterRequests.ViewHolder holder, final int position) {

        final FriendsRequestsForRecyclerView friendsRequestsForRecyclerView = friendsRequestsForRecyclerViews.get(position);


        mSettings = getDefaultSharedPreferences(friendsRequestsForRecyclerView.getContext());
        email = mSettings.getString("email","");
        counterFor = 1;

        holder.emailView.setText(friendsRequestsForRecyclerView.getEmail());
        holder.confirm.setText(friendsRequestsForRecyclerView.getConfirm());

        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(friendsRequestsForRecyclerView.getContext(),"fghghg",Toast.LENGTH_LONG).show();
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if (counterFor == 1){
                            try{
                                //Добавление друга
                                count = dataSnapshot.child(holder.emailView.getText()+"").child("friends").child("count").getValue(Integer.class);
                                myRef.child(holder.emailView.getText()+"").child("friends").child(String.valueOf(count)).setValue(email);
                                myRef.child(holder.emailView.getText()+"").child("friends").child("count").setValue(count+1);


                                count = dataSnapshot.child(email).child("friends").child("count").getValue(Integer.class);
                                myRef.child(email).child("friends").child(String.valueOf(count)).setValue(holder.emailView.getText()+"");
                                myRef.child(email).child("friends").child("count").setValue(count+1);




                            } catch (Exception e) {

                            }
                            counterFor = 0;
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                    }
                });
                Intent intent = new Intent(friendsRequestsForRecyclerView.getActivity(), FriendRequest.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(friendsRequestsForRecyclerView.getContext(),intent,null);

            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsRequestsForRecyclerViews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView emailView, confirm;
        ViewHolder(View view){
            super(view);
            emailView = (TextView) view.findViewById(R.id.email);
            confirm = (TextView) view.findViewById(R.id.confirm);
        }
    }
}
