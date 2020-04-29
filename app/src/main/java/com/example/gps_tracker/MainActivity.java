package com.example.gps_tracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    public static boolean geolocationEnabled = false;
    int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;
    DatabaseReference myRef;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        //смотрим первый ли раз мы зашли в приложение, если первый то добавляем информацию в бд

        SharedPreferences mSettings = getDefaultSharedPreferences(this);
        boolean first = mSettings.getBoolean("first",false);
        String mLogin = mSettings.getString("emailForBD","");
        if (first){
            myRef.child(mLogin.replace(".","").toLowerCase()).child("name").setValue(mSettings.getString("nick",""));
            myRef.child(mLogin.replace(".","").toLowerCase()).child("currentLocation").child("longitude").setValue(null);
            myRef.child(mLogin.replace(".","").toLowerCase()).child("currentLocation").child("latitude").setValue(null);
            myRef.child(mLogin.replace(".","").toLowerCase()).child("sendRequests").child("count").setValue(0);
            myRef.child(mLogin.replace(".","").toLowerCase()).child("receiveRequests").child("count").setValue(0);
            myRef.child(mLogin.replace(".","").toLowerCase()).child("friends").child("count").setValue(0);
            String defaultImage = "gs://gps-tracker-275108.appspot.com"+"default";
            myRef.child(mLogin.replace(".","").toLowerCase()).child("photo").setValue(defaultImage);

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("first",false);
            editor.apply();
        }


        //options нужны, чтобы понять какой фрагмент открывать, в зависимости от того, откуда мы приходим в MainActivity
        Intent intent = getIntent();
        String options = intent.getStringExtra("options");
        if (options == null){
            options = "";
        }

        Fragment fragment;

        //получаем разрешение на местоположение от пользователя
        int permissionStatus = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);

            //открываем профиль пользователя
            navigation.getMenu().getItem(0).setChecked(true);
            FragmentManager fragmentManager;
            fragment = new FriendsFragment();
            fragmentManager = MainActivity.this.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
        }

        checkLocationServiceEnabled();

        //в зависимости от значения options открываем нужный фрагмент
        if(permissionStatus == PackageManager.PERMISSION_GRANTED){

            if (options.equals("map")){

                navigation.getMenu().getItem(1).setChecked(true);
                FragmentManager fragmentManager;
                fragment = new MapFragment();
                fragmentManager = MainActivity.this.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();

            }
            else{

                if (options.equals("friends")){

                    navigation.getMenu().getItem(0).setChecked(true);
                    FragmentManager fragmentManager;
                    fragment = new FriendsFragment();
                    fragmentManager = MainActivity.this.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();

                }else{

                    navigation.getMenu().getItem(0).setChecked(true);
                    FragmentManager fragmentManager;
                    fragment = new FriendsFragment();
                    fragmentManager = MainActivity.this.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();

                }
            }

        }

    }
    //функция отображения нижнего меню
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

    //обработчик нажатия кнопки назад
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    //функция для проверки включено ли местоположение
    private boolean checkLocationServiceEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            geolocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        return buildAlertMessageNoLocationService(geolocationEnabled);
    }

    //функция открыти диалога для включения местоположения
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted

                } else {
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());

                }
                return;
        }
    }

}
