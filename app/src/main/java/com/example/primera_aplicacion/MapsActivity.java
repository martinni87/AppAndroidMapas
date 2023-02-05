package com.example.primera_aplicacion;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.example.primera_aplicacion.fragments.HomeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.primera_aplicacion.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Solicitamos permisos al cargar la activity de mapas.
        this.setupPermissionsLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_maps);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            loadMyLastLocation(googleMap);
            loadUserLocation(googleMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Método para solicitar permisos de localización.
    public void setupPermissionsLocation() {
        ActivityResultLauncher<String[]> locationPermissionsRequest =
                registerForActivityResult(
                        new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                            if (fineLocationGranted != null && coarseLocationGranted != null) {
                                //Tenemos permiso de ubicación de ambos
                            } else if (coarseLocationGranted != null) {
                                //Tenemos permiso de ubicación aproximada.
                            } else {
                                //No tenemos permisos de ubicación
                            }
                        }
                );
        locationPermissionsRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    public void loadMyLastLocation(GoogleMap googleMap) {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        //Cogemos la última localización.
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            LatLng myLocation = new LatLng(0,0);
            googleMap.addMarker(new MarkerOptions().position(myLocation));
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(myLocation).title("Estás aquí"));
            }
        });
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
    }

    public void loadUserLocation(GoogleMap googleMap) throws IOException {
        //Creamos el intent heredando valores de la activity anterior, y un nuevo bundle con los datos.
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        String name = bundle.getString("nombre") + " "+ bundle.getString("apellidos");
        String address = bundle.getString("ciudad") + " " + bundle.getString("provincia");

        LatLng userLocation = getLatLng(address);

        googleMap.addMarker(new MarkerOptions().position(userLocation).title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        googleMap.setOnInfoWindowClickListener(marker -> {
            startActivity(new Intent(getApplicationContext(),TempResultsActivity.class).putExtras(bundle));
        });
    }

    public LatLng getLatLng(String location) throws IOException {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addressList = geocoder.getFromLocationName(location,1);
        double latitude = addressList.get(0).getLatitude();
        double longitude = addressList.get(0).getLongitude();

        return new LatLng(latitude, longitude);
    }

}