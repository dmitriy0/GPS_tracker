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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AddFriend extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");

    String friendEmail;
    String email;
    String name;
    int count;
    int counterFor;
    boolean permission;

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mSettings = getDefaultSharedPreferences(this);

        //обработчик нажатия на стрелочку
        final ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddFriend.this, MainActivity.class);
                intent.putExtra("options","friends");
                startActivity(intent);

            }
        });

        //Кнопка отправления запроса дружбы
        Button add = (Button) findViewById(R.id.addFriend);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendEmail = ((EditText) findViewById(R.id.friendEmail)).getText().toString().toLowerCase().replace(".","");
                email = mSettings.getString("email","");
                counterFor = 1;

                if (email.equals(friendEmail)){

                    Toast.makeText(AddFriend.this,"это ваш email",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddFriend.this, AddFriend.class);
                    startActivity(intent);

                }

                else{

                    permission = true;
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            if (counterFor == 1){

                                //получаю имя пользователя с заданной почтой, если имя = null то выводим toast
                                name = dataSnapshot.child(friendEmail.replace(".","")).child("name").getValue(String.class);
                                if (name == null){

                                    Toast.makeText(getApplicationContext(),"пользователя с такими данными не существует",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(AddFriend.this, AddFriend.class);
                                    startActivity(intent);

                                }
                                else{
                                    //проверяю не добавлен ли этот пользователь ко мне в друзья
                                    int countFriends = dataSnapshot.child(email).child("friends").child("count").getValue(Integer.class);
                                    for (int i = 0; i < countFriends;i++) {

                                        String mFriendEmail = dataSnapshot.child(email).child("friends").child(i + "").getValue(String.class);
                                        if (friendEmail.equals(mFriendEmail)){

                                            Toast.makeText(getApplicationContext(),"данный пользователь у вас в друзьях",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(AddFriend.this, AddFriend.class);
                                            startActivity(intent);
                                            permission = false;

                                        }
                                    }
                                    if (permission){

                                        //проверяю не отправил ли этот пользователь мне запрос дружбы
                                        int countMyRequests = dataSnapshot.child(email).child("requests").child("count").getValue(Integer.class);
                                        for (int i = 0; i < countMyRequests;i++) {
                                            String requestEmail = dataSnapshot.child(email).child("requests").child(i + "").getValue(String.class);
                                            if (friendEmail.equals(requestEmail)){
                                                Toast.makeText(getApplicationContext(),"данный пользователь уже отправил вам запрос дружбы",Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(AddFriend.this, AddFriend.class);
                                                intent.putExtra("options","friends");
                                                startActivity(intent);
                                                permission = false;
                                            }
                                        }
                                    }
                                    if (permission) {
                                        //проверяю не отправил ли я этому пользователю запрос дружбы
                                        int countFriendRequests = dataSnapshot.child(friendEmail).child("requests").child("count").getValue(Integer.class);
                                        for (int i = 0; i < countFriendRequests; i++) {
                                            String friendRequests = dataSnapshot.child(friendEmail).child("requests").child(i + "").getValue(String.class);
                                            if (email.equals(friendRequests)) {
                                                Toast.makeText(getApplicationContext(), "вы уже отправили запрос дружбы ему", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(AddFriend.this, AddFriend.class);
                                                startActivity(intent);
                                                permission = false;
                                            }
                                        }
                                    }

                                    //отправляю запрос дружбы
                                    if(permission){

                                        count = dataSnapshot.child(friendEmail.replace(".","")).child("requests").child("count").getValue(Integer.class);
                                        myRef.child(friendEmail.replace(".","")).child("requests").child(String.valueOf(count)).setValue(email);
                                        myRef.child(friendEmail.replace(".","")).child("requests").child("count").setValue(count+1);
                                        Toast.makeText(getApplicationContext(),"запрос отправлен",Toast.LENGTH_LONG).show();

                                    }
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

            }
        });
    }
    //обработчик нажатия кнопки назад
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(AddFriend.this, MainActivity.class);
        startActivity(intent);
    }
}
