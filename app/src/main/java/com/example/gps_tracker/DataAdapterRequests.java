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
import static androidx.core.content.ContextCompat.getMainExecutor;
import static androidx.core.content.ContextCompat.startActivity;

class DataAdapterRequests extends RecyclerView.Adapter<DataAdapterRequests.ViewHolder> {

    //класс который нужен для recyclerView запросов

    private LayoutInflater inflater;
    private List<FriendsRequestsForRecyclerView> friendsRequestsForRecyclerViews;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    private SharedPreferences mSettings;

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

        holder.emailView.setText(friendsRequestsForRecyclerView.getEmail());

        mSettings = getDefaultSharedPreferences(friendsRequestsForRecyclerView.getContext());
        email = mSettings.getString("emailForBD","");

        counterFor = 1;

        //принятие запроса от друга
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                                int countReceiveRequests = dataSnapshot.child(email).child("receiveRequests").child("count").getValue(Integer.class);
                                myRef.child(email).child("receiveRequests").child(String.valueOf(position)).removeValue();
                                for (int j = position+1; j < countReceiveRequests;j++) {
                                    String b = dataSnapshot.child(email).child("receiveRequests").child(String.valueOf(j)).getValue(String.class);
                                    myRef.child(email).child("receiveRequests").child(String.valueOf(j-1)).setValue(b);

                                }
                                myRef.child(email).child("receiveRequests").child(String.valueOf(countReceiveRequests-1)).removeValue();
                                myRef.child(email).child("receiveRequests").child("count").setValue(countReceiveRequests-1);

                                int countSendRequests = dataSnapshot.child(holder.emailView.getText()+"").child("sendRequests").child("count").getValue(Integer.class);
                                myRef.child(holder.emailView.getText()+"").child("sendRequests").child(String.valueOf(position)).removeValue();
                                for (int j = position+1; j < countSendRequests;j++) {
                                    String b = dataSnapshot.child(holder.emailView.getText()+"").child("sendRequests").child(String.valueOf(j)).getValue(String.class);
                                    myRef.child(holder.emailView.getText()+"").child("sendRequests").child(String.valueOf(j-1)).setValue(b);

                                }
                                myRef.child(holder.emailView.getText()+"").child("sendRequests").child(String.valueOf(countSendRequests-1)).removeValue();
                                myRef.child(holder.emailView.getText()+"").child("sendRequests").child("count").setValue(countSendRequests-1);

                                Toast.makeText(friendsRequestsForRecyclerView.getContext(),"пользователь добавлен в ваш список друзей",Toast.LENGTH_LONG).show();

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
        final TextView emailView;
        final ImageView confirm;
        ViewHolder(View view){
            super(view);
            emailView = (TextView) view.findViewById(R.id.email);
            confirm = (ImageView) view.findViewById(R.id.confirm);
        }
    }
}
