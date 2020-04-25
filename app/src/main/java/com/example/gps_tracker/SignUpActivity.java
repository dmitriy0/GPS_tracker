package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SignUpActivity extends AppCompatActivity {
    String mLogin;
    String mPassword;
    String mRepeatPassword;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference myRef;

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mSettings = getDefaultSharedPreferences(this);

        Button singUp = (Button) findViewById(R.id.signUp); // кнопка регистрации
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin = ((EditText) findViewById(R.id.login)).getText().toString();
                mPassword = ((EditText) findViewById(R.id.password)).getText().toString();
                mRepeatPassword = ((EditText) findViewById(R.id.repeatPassword)).getText().toString();
                if("".equals(mLogin) || "".equals(mPassword) ) {
                    Toast.makeText(SignUpActivity.this, "Одно из полей не заполненно. Пожалуйста, заполните все поля и повторите отправку", Toast.LENGTH_LONG).show();
                }
                else {
                    if (mPassword.equals(mRepeatPassword)){
                        addUser();
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Пароли не совпадают. Пожалуйста, повторите попытку", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
    private void addUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(mLogin, mPassword).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Регистрация успешна", Toast.LENGTH_LONG).show();

                    myRef.child(mLogin.replace(".","").toLowerCase()).child("name").setValue("amid");
                    myRef.child(mLogin.replace(".","").toLowerCase()).child("currentLocation").setValue("loc");
                    myRef.child(mLogin.replace(".","").toLowerCase()).child("requests").child("count").setValue(0);
                    myRef.child(mLogin.replace(".","").toLowerCase()).child("friends").child("count").setValue(0);


                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("email",mLogin.replace(".","").toLowerCase());
                    editor.apply();

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Регистрация провалена", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
