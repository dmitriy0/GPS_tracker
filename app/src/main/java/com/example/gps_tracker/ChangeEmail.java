package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ChangeEmail extends AppCompatActivity {

    String email;
    String currentEmail;
    String currentPassword;
    String newEmail;
    int counterFor;
    int countFriends;
    int countReceiveRequests;
    int countSendRequests;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        counterFor = 1;

        preferences = getDefaultSharedPreferences(this);
        email = preferences.getString("emailForBD","");
        currentEmail = preferences.getString("realEmail","");

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Button saveChanges = (Button) findViewById(R.id.save);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPassword = ((EditText) findViewById(R.id.currentPassword)).getText().toString();
                newEmail = ((EditText) findViewById(R.id.newEmail)).getText().toString();

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(currentEmail, currentPassword);

// Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.updateEmail(newEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    myRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            // This method is called once with the initial value and again
                                                            // whenever data at this location is updated.
                                                            if (counterFor == 1){
                                                                try{

                                                                    newEmail = newEmail.replace(".","").toLowerCase();

                                                                    countReceiveRequests = dataSnapshot.child(email).child("receiveRequests").child("count").getValue(Integer.class);
                                                                    for (int i = 0; i < countReceiveRequests;i++) {

                                                                        String friendEmail = dataSnapshot.child(email).child("receiveRequests").child(i + "").getValue(String.class);
                                                                        int countFriendSendRequests = dataSnapshot.child(friendEmail).child("sendRequests").child("count").getValue(Integer.class);
                                                                        for (int j = 0; j < countFriendSendRequests;j++) {
                                                                            if (dataSnapshot.child(friendEmail).child("sendRequests").child(j+"").getValue(String.class).equals(email)){
                                                                                myRef.child(friendEmail).child("sendRequests").child(j+"").setValue(newEmail);
                                                                            }
                                                                        }
                                                                        myRef.child(newEmail).child("receiveRequests").child(i+"").setValue(friendEmail);
                                                                    }

                                                                    countSendRequests = dataSnapshot.child(email).child("sendRequests").child("count").getValue(Integer.class);
                                                                    for (int i = 0; i < countReceiveRequests;i++) {

                                                                        String friendEmail = dataSnapshot.child(email).child("sendRequests").child(i + "").getValue(String.class);
                                                                        int countFriendReceiveRequests = dataSnapshot.child(friendEmail).child("receiveRequests").child("count").getValue(Integer.class);
                                                                        for (int j = 0; j < countFriendReceiveRequests;j++) {
                                                                            if (dataSnapshot.child(friendEmail).child("receiveRequests").child(j+"").getValue(String.class).equals(email)){
                                                                                myRef.child(friendEmail).child("receiveRequests").child(j+"").setValue(newEmail);
                                                                            }
                                                                        }
                                                                        myRef.child(newEmail).child("sendRequests").child(i+"").setValue(friendEmail);
                                                                    }


                                                                    countFriends = dataSnapshot.child(email).child("friends").child("count").getValue(Integer.class);
                                                                    for (int i = 0; i < countFriends;i++) {

                                                                        String friendEmail = dataSnapshot.child(email).child("friends").child(i + "").getValue(String.class);
                                                                        myRef.child(friendEmail).child("friends").child(i+"").setValue(newEmail);
                                                                        myRef.child(newEmail).child("friends").child(i+"").setValue(friendEmail);

                                                                    }


                                                                    Double lat = dataSnapshot.child(email).child("currentLocation").child("latitude").getValue(Double.class);
                                                                    Double lng = dataSnapshot.child(email).child("currentLocation").child("longitude").getValue(Double.class);
                                                                    String photo = dataSnapshot.child(email).child("photo").getValue(String.class);
                                                                    String name = dataSnapshot.child(email).child("name").getValue(String.class);

                                                                    myRef.child(newEmail).child("currentLocation").child("latitude").setValue(lat);
                                                                    myRef.child(newEmail).child("currentLocation").child("longitude").setValue(lng);
                                                                    myRef.child(newEmail).child("name").setValue(name);
                                                                    myRef.child(newEmail).child("photo").setValue(photo);
                                                                    myRef.child(newEmail).child("sendRequests").child("count").setValue(countSendRequests);
                                                                    myRef.child(newEmail).child("receiveRequests").child("count").setValue(countReceiveRequests);
                                                                    myRef.child(newEmail).child("friends").child("count").setValue(countFriends);

                                                                    myRef.child(email).removeValue();

                                                                    SharedPreferences.Editor editor = preferences.edit();
                                                                    editor.putString("emailForBD",newEmail.replace(".","").toLowerCase());
                                                                    editor.putString("realEmail",newEmail);
                                                                    editor.apply();



                                                                }
                                                                catch (Exception e) {
                                                                    Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
                                                                }


                                                                Toast.makeText(getApplicationContext(),"sucsesful",Toast.LENGTH_LONG).show();

                                                                counterFor = 0;
                                                            }
                                                            Intent intent = new Intent(ChangeEmail.this, SignInActivity.class);
                                                            intent.putExtra("options",false);
                                                            startActivity(intent);



                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            // Failed to read value
                                                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        });




            }
        });
    }
}

