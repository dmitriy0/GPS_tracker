package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    String mNickname;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference myRef;

    boolean isEmailExist;

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mSettings = getDefaultSharedPreferences(this);

        //обработчик нажатия на текст "Войти"
        TextView enter = (TextView) findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        Button singUp = (Button) findViewById(R.id.signUp); // кнопка регистрации
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin = ((EditText) findViewById(R.id.login)).getText().toString();
                mPassword = ((EditText) findViewById(R.id.password)).getText().toString();
                mNickname = ((EditText) findViewById(R.id.nickName)).getText().toString();
                mRepeatPassword = ((EditText) findViewById(R.id.repeatPassword)).getText().toString();
                if("".equals(mLogin) || "".equals(mPassword) || "".equals(mNickname) || "".equals(mRepeatPassword)) {
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

    //Функция регистрации
    private void addUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        isEmailExist = false;



        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(mLogin, mPassword).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();

                    //отправка проверочного письма
                    mUser.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // email sent

                                        Toast.makeText(getBaseContext(),"на ваш email отправлено письмо для подтверждения регистрации",Toast.LENGTH_LONG).show();
                                        SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putBoolean("first",true);
                                        editor.putString("nick",mNickname);
                                        editor.apply();
                                        // after email is sent just logout the user and finish this activity
                                        mAuth.signOut();
                                        finish();
                                    }
                                    else
                                    {
                                        // email not sent, so display message and restart the activity or do whatever you wish to do

                                        //restart this activity


                                    }
                                }
                            });

                }


                 else {
                    Toast.makeText(SignUpActivity.this, "регистрация провалена", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //обработчик нажатия кнопки назад
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
