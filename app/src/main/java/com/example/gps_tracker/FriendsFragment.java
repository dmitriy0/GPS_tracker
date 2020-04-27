package com.example.gps_tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class FriendsFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");

    private String friendEmail;
    private String friendName;
    private String email;
    private int count;
    private int counterFor;
    private Double friendLongitude;
    private Double friendLatitude;

    private SharedPreferences preferences;

    private StorageReference mStorageRef;

    private List<FriendsForRecyclerView> friends = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        //кнопка перехода в активити отправления запроса дружбы
        final Button addFriend = (Button) rootView.findViewById(R.id.addFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddFriend.class);
                startActivity(intent);

            }
        });

        //кнопка перехода в активити принятия запроса дружбы
        final Button confirm = (Button) rootView.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FriendRequest.class);
                startActivity(intent);

            }
        });

        preferences = getDefaultSharedPreferences(getContext());
        email = preferences.getString("email","");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        counterFor = 1;
        //получение из бд списка друзей
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (counterFor == 1) {

                    try {
                        count = dataSnapshot.child(email).child("friends").child("count").getValue(Integer.class);
                        int countRequests = dataSnapshot.child(email).child("requests").child("count").getValue(Integer.class);
                        confirm.setText("Принять запросы ("+countRequests+")");
                        for (int i = 0; i < count; i++) {

                            friendEmail = dataSnapshot.child(email).child("friends").child(String.valueOf(i)).getValue(String.class);

                            final String imagePath = dataSnapshot.child(friendEmail).child("photo").getValue(String.class);

                            friendName = dataSnapshot.child(friendEmail).child("name").getValue(String.class);
                            friendLongitude = dataSnapshot.child(friendEmail).child("currentLocation").child("longitude").getValue(Double.class);
                            friendLatitude = dataSnapshot.child(friendEmail).child("currentLocation").child("latitude").getValue(Double.class);

                            friends.add(new FriendsForRecyclerView(friendName, friendEmail, imagePath,getActivity(),friendLongitude,friendLatitude));

                        }
                        DataAdapter adapter = new DataAdapter(getContext(), friends);
                        recyclerView.setAdapter(adapter);


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

        return rootView;
    }


}