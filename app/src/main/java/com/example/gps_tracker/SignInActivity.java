package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Objects;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SignInActivity extends AppCompatActivity {

    String mLogin;
    String mPassword;

    private FirebaseAuth mAuth;

    DatabaseReference myRef;
    FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettings = getDefaultSharedPreferences(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //если пользователь уже вошел ранее пропускаем его дальше
        if (user != null) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {

            setContentView(R.layout.activity_sign_in);

            Button singIn = (Button) findViewById(R.id.signIn); // кнопка входа
            singIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLogin = ((EditText) findViewById(R.id.login)).getText().toString();
                    mPassword = ((EditText) findViewById(R.id.password)).getText().toString();
                    if("".equals(mLogin) || "".equals(mPassword)) {
                        Toast.makeText(SignInActivity.this, "Одно из полей не заполненно. Пожалуйста, заполните все поля и повторите отправку", Toast.LENGTH_LONG).show();
                    }
                    else {
                        singInUser();
                    }

                }
            });
            //обработчик нажатия на текст "Зарегистрироваться"
            TextView singUp = (TextView) findViewById(R.id.newAccount);
            singUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


    //функция входа
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void singInUser(){
        mAuth = FirebaseAuth.getInstance();


        mAuth.signInWithEmailAndPassword(mLogin, mPassword).addOnCompleteListener(Objects.requireNonNull(SignInActivity.this), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    if(Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()){
                        Toast.makeText(SignInActivity.this, "Авторизация успешна", Toast.LENGTH_LONG).show();




                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("emailForBD",mLogin.replace(".","").toLowerCase());
                        editor.putString("realEmail",mLogin);
                        editor.apply();
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);







                        //Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        // startActivity(intent);


                    }else{
                        Toast.makeText(SignInActivity.this, "подтвердите свой email", Toast.LENGTH_LONG).show();

                    }


                }
                else{
                    Toast.makeText(SignInActivity.this, "Авторизация провалена", Toast.LENGTH_LONG).show();
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
