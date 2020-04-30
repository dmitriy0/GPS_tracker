package com.example.gps_tracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static androidx.core.content.ContextCompat.checkSelfPermission;


public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");
    private SharedPreferences preferences;

    private String email;
    private int permissionStatus;
    private int count;
    private Double friendLat;
    private Double friendLng;

    Location myLocation;
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        preferences = getDefaultSharedPreferences(getContext());
        email = preferences.getString("emailForBD","");

        //проверяем получено ли разрешение на местоположение от пользователя
        permissionStatus = checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED){
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }

        return rootView;
    }

    //функция для отображения карты
    @Override
    public void onMapReady(final GoogleMap googleMap) {



        Intent intent = requireActivity().getIntent();
        friendLat = (Objects.requireNonNull(intent.getDoubleExtra("lat",0.0)));
        friendLng = (Objects.requireNonNull(intent.getDoubleExtra("lng",0.0)));

        if (permissionStatus == PackageManager.PERMISSION_GRANTED)
        {
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.setMyLocationEnabled(true);

            /*FusedLocationProviderClient userLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            userLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){

                        myRef.child(email).child("currentLocation").child("longitude").setValue(location.getLongitude());
                        myRef.child(email).child("currentLocation").child("latitude").setValue(location.getLatitude());

                        //анимация камеры к моему местоположению
                        if (friendLat == 0.0 && friendLng == 0.0) {
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(myLocation)
                                    .zoom(9)
                                    .build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            googleMap.animateCamera(cameraUpdate);
                        }
                    }


                }
            });
            
             */
            //анимация камеры к местоположению друга
            if (friendLat != 0.0 && friendLng != 0.0){
                LatLng friendLocation = new LatLng(friendLat,friendLng);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(friendLocation)
                        .zoom(9)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                googleMap.animateCamera(cameraUpdate);
                intent.removeExtra("lat");
                intent.removeExtra("lng");
            }

            //отображение местоположения друзей
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //очищаем карту и отрисовываем маркеры друзей
                    googleMap.clear();
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try{
                        count = dataSnapshot.child(email).child("friends").child("count").getValue(Integer.class);
                        for (int i = 0; i < count;i++) {
                            String friendEmail = dataSnapshot.child(email).child("friends").child(String.valueOf(i)).getValue(String.class);
                            assert friendEmail != null;
                            try{
                                Double friendLongitude = dataSnapshot.child(friendEmail).child("currentLocation").child("longitude").getValue(Double.class);
                                Double friendLatitude = dataSnapshot.child(friendEmail).child("currentLocation").child("latitude").getValue(Double.class);
                                LatLng friend = new LatLng(friendLatitude, friendLongitude);
                                googleMap.addMarker(new MarkerOptions().position(friend).title(friendEmail));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }




                    } catch (Exception e) {

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                }
            });
        }

    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


}