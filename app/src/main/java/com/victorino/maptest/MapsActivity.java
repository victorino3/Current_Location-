package com.victorino.maptest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.victorino.maptest.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;


    //Create an Array to save the permission
    private String[] permission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    //Create a class that allow us to get user location
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, new MapRendererOptInApplication());
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Override validatePermission method
        Permissoes.validatePermission(permission,this,1);
        // Construct a PlacesClient
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.print(location);
                Log.d("LocationTosSet", String.format("From onLocationChanged %s", location));

            }
        };
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {



        /*LatLng userLocationX = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(userLocationX).title("My location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationX,10));
        Log.d("Current LocationX", String.format("From onLocationChanged %s %s", latitude, longitude));*/
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //Set locationManager and locationListener 40.27925451053817, -7.504783596524472
            LocationTrack locationTrack = new LocationTrack(this);
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            if(addressList != null && addressList.size() > 0){
                Address result = addressList.get(0);
                Log.d("Local return","onLocationChanged"+result.getAddressLine(0));
                double latitudeToMarker = result.getLatitude();
                double longitudeToMarker = result.getLongitude();
                //remove old point to add new
                //mMap.clear();
                LatLng userLocationX = new LatLng(latitudeToMarker, longitudeToMarker);
                mMap.addMarker(new MarkerOptions().position(userLocationX).title(result.getAddressLine(0)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationX,10));
            }
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("LocationTosSet", String.format("From onLocationChanged %s", location));

                }
            };
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        } catch (IOException e) {
            e.printStackTrace();

        }


        /*Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String place = "Universidade da beira interior";
        try {
            List<Address> addressList = geocoder.getFromLocationName(place,1);
            if(addressList != null && addressList.size() > 0){
                Address result = addressList.get(0);
                Log.d("Local return","onLocationChanged"+result.getAddressLine(0));
                double latitude = result.getLatitude();
                double longitude = result.getLongitude();
                //remove old point to add new
                //mMap.clear();
                LatLng userLocationX = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(userLocationX).title(result.getAddressLine(0)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationX,10));
            }
        } catch (IOException e) {
            e.printStackTrace();
             -122.084 37.421998333333335
        }*/
        // Add a marker in Sydney and move the camera

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissionResult : grantResults){
            if(permissionResult == PackageManager.PERMISSION_DENIED){
                sendWarning();
            }else if(permissionResult == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }

            }
        }

    }
    public void sendWarning(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Permissions Denied");
        dialog.setMessage("To use this functionality necessary to accept permission!");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog apply = dialog.create();
        apply.show();
    }
}