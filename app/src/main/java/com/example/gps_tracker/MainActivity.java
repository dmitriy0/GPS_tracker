package com.example.gps_tracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //получаем разрешение на местоположение от пользователя
        int permissionStatus = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().getItem(0).setChecked(true);
        Fragment fragment;

        FragmentManager fragmentManager;
        fragment = new FriendsFragment();
        fragmentManager = MainActivity.this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
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

}
