package com.example.gps_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //получаем разрешение на местоположение от пользователя
        int permissionStatus = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);

            navigation.getMenu().getItem(2).setChecked(true);
            Fragment fragment;
            FragmentManager fragmentManager;
            fragment = new ProfileFragment();
            fragmentManager = MainActivity.this.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();

        }



        Fragment fragment;
        if(permissionStatus == PackageManager.PERMISSION_GRANTED){
            navigation.getMenu().getItem(1).setChecked(true);
            FragmentManager fragmentManager;
            fragment = new MapFragment();
            fragmentManager = MainActivity.this.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FragmentManager fragmentManager;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new FriendsFragment();
                    fragmentManager = MainActivity.this.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragment = new MapFragment();
                    fragmentManager = MainActivity.this.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    fragment = new ProfileFragment();
                     fragmentManager = MainActivity.this.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
                    return true;
            }
            return false;
        }
    };
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

}
