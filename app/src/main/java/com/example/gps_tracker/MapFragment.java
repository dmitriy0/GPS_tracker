package com.example.gps_tracker;

import android.Manifest;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static androidx.core.content.ContextCompat.checkSelfPermission;


public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    Location myLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Users");
    private SharedPreferences preferences;
    private int permissionStatus;
    private int count;
    private String email;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        preferences = getDefaultSharedPreferences(getContext());

        preferences = getDefaultSharedPreferences(getContext());
        email = preferences.getString("email","");
        permissionStatus = checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED){
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }




        return rootView;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera
        if (permissionStatus == PackageManager.PERMISSION_GRANTED)
        {
            googleMap.setOnMyLocationClickListener(this);
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.setMyLocationEnabled(true);
            FusedLocationProviderClient userLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            userLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null){
                        Toast.makeText(getContext(), "Пожалуйста, включите местоположение", Toast.LENGTH_LONG).show();
                    }
                    else {
                        myRef.child(email).child("currentLocation").child("longitude").setValue(location.getLongitude());
                        myRef.child(email).child("currentLocation").child("latitude").setValue(location.getLatitude());

                    }

                }
            });
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try{
                        //Добавление друга
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
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


}