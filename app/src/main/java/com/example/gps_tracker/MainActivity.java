package com.example.gps_tracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    public static boolean geolocationEnabled = false;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Fragment fragment;

        //получаем разрешение на местоположение от пользователя
        int permissionStatus = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
            navigation.getMenu().getItem(2).setChecked(true);
            FragmentManager fragmentManager;
            fragment = new ProfileFragment();
            fragmentManager = MainActivity.this.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
        }

        checkLocationServiceEnabled();


        if(permissionStatus == PackageManager.PERMISSION_GRANTED){
            navigation.getMenu().getItem(2).setChecked(true);
            FragmentManager fragmentManager;
            fragment = new ProfileFragment();
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
    private boolean checkLocationServiceEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            geolocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        return buildAlertMessageNoLocationService(geolocationEnabled);
    }

    /**
     *  Показываем диалог и переводим пользователя к настройкам геолокации
     */
    private boolean buildAlertMessageNoLocationService(boolean network_enabled) {
        String msg = !network_enabled ? "Чтобы продолжить, включите на устройстве геолокацию" : null;

        if (msg != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setMessage(msg)
                    .setPositiveButton("Включить", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return false;
    }

}
