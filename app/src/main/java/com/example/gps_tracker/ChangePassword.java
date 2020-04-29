package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ChangePassword extends AppCompatActivity {

    String currentPassword;
    String newPassword;
    String repeatPassword;
    String email;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        preferences = getDefaultSharedPreferences(this);
        email = preferences.getString("realEmail","");

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

        Button saveChanges = (Button) findViewById(R.id.save);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPassword = ((EditText) findViewById(R.id.currentPassword)).getText().toString();
                newPassword = ((EditText) findViewById(R.id.newPassword)).getText().toString();
                repeatPassword = ((EditText) findViewById(R.id.repeatPassword)).getText().toString();
                if (newPassword.equals(repeatPassword)) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, currentPassword);

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getBaseContext(),"пароль успешно изменен",Toast.LENGTH_LONG).show();
                                                    Intent mStartActivity = new Intent(getApplicationContext(), SignInActivity.class);
                                                    int mPendingIntentId = 123456;
                                                    PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                                    AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                                    System.exit(0);
                                                } else {
                                                    Toast.makeText(getApplicationContext(),"ошибка",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(getApplicationContext(),"пароли не совпадают",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

