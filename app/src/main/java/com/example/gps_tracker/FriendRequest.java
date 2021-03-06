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

    String friendEmail;
    String email;
    int countFriends;
    int countReceiveRequests;
    int counterFor;
    boolean isRequestInFriends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mSettings = getDefaultSharedPreferences(this);
        email = mSettings.getString("emailForBD","");

        counterFor = 1;

        //обработчик нажатия на стрелочку
        final ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FriendRequest.this, MainActivity.class);
                intent.putExtra("options","friends");
                startActivity(intent);

            }
        });

        isRequestInFriends = false;

        //получение из бд и вывод всех запросов дружбы
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (counterFor == 1){

                    try{
                        countReceiveRequests = dataSnapshot.child(email).child("receiveRequests").child("count").getValue(Integer.class);

                        for (int i = 0; i < countReceiveRequests;i++) {

                            friendEmail = dataSnapshot.child(email).child("receiveRequests").child(i+"").getValue(String.class);
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
                                DataAdapterRequests adapter = new DataAdapterRequests(FriendRequest.this, friendsRequestsForRecyclerViews);
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
    //обработчик нажатия кнопки назад
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(FriendRequest.this, MainActivity.class);
        startActivity(intent);
    }
}
