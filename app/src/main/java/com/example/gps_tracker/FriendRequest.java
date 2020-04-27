package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class FriendRequest extends AppCompatActivity {

    List<FriendsRequestsForRecyclerView> friendsRequestsForRecyclerViews = new ArrayList<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");

    private SharedPreferences mSettings;
    String email;
    int countFriends;
    int countRequests;
    int counterFor;
    boolean isRequestInFriends;
    String friendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        mSettings = getDefaultSharedPreferences(this);



        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        email = mSettings.getString("email","");

        counterFor = 1;

        final ImageView confirm = (ImageView) findViewById(R.id.back); // кнопка
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FriendRequest.this, MainActivity.class);
                intent.putExtra("options","friends");
                startActivity(intent);

            }
        });

        isRequestInFriends = false;

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (counterFor == 1){
                    try{
                        countRequests = dataSnapshot.child(email).child("requests").child("count").getValue(Integer.class);

                        for (int i = 0; i < countRequests;i++) {
                            friendEmail = dataSnapshot.child(email).child("requests").child(i+"").getValue(String.class);
                            isRequestInFriends = false;
                            countFriends = dataSnapshot.child(email).child("friends").child("count").getValue(Integer.class);
                            for (int j = 0; j < countFriends;j++) {
                                String myFriends = dataSnapshot.child(email).child("friends").child(String.valueOf(j)).getValue(String.class);
                                assert friendEmail != null;
                                if (friendEmail.equals(myFriends)){

                                    isRequestInFriends = true;
                                }
                            }
                            if (!isRequestInFriends){

                                friendsRequestsForRecyclerViews.add(new FriendsRequestsForRecyclerView(friendEmail,getApplicationContext(),FriendRequest.this));
                                // создаем адаптер
                                DataAdapterRequests adapter = new DataAdapterRequests(FriendRequest.this, friendsRequestsForRecyclerViews);
                                // устанавливаем для списка адаптер
                                recyclerView.setAdapter(adapter);
                            }
                        }





                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
                    }
                    counterFor = 0;
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
            }
        });



    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(FriendRequest.this, MainActivity.class);
        startActivity(intent);
    }
}
