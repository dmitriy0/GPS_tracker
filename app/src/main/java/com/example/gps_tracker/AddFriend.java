package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AddFriend extends AppCompatActivity {

    String friendEmail;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");
    private SharedPreferences mSettings;
    String email;
    int count;
    String name;
    int counterFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mSettings = getDefaultSharedPreferences(this);


        Button add = (Button) findViewById(R.id.addFriend); // кнопка добавления друга
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendEmail = ((EditText) findViewById(R.id.friendEmail)).getText().toString().toLowerCase();
                counterFor = 1;
                email = mSettings.getString("email","");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if (counterFor == 1){
                            name = dataSnapshot.child(friendEmail.replace(".","")).child("name").getValue(String.class);
                            if (name == null){
                                Toast.makeText(getApplicationContext(),"пользователя с такими данными не существует",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AddFriend.this, AddFriend.class);
                                startActivity(intent);
                            }
                            else{
                                count = dataSnapshot.child(friendEmail.replace(".","")).child("requests").child("count").getValue(Integer.class);
                                myRef.child(friendEmail.replace(".","")).child("requests").child(String.valueOf(count)).setValue(email);
                                myRef.child(friendEmail.replace(".","")).child("requests").child("count").setValue(count+1);
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

                Intent intent = new Intent(AddFriend.this, MainActivity.class);
                startActivity(intent);




            }
        });
    }
}
