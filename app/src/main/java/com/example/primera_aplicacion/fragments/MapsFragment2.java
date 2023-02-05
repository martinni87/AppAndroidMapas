package com.example.primera_aplicacion.fragments;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.primera_aplicacion.MapsActivity;
import com.example.primera_aplicacion.R;
import com.example.primera_aplicacion.TempResultsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MapsFragment2 extends Fragment {

    private final OnMapReadyCallback callback = googleMap -> {
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //El siguiente método se ha cogido de otra activity... Esto es mala práctica, pendiente crear método en una clase
        //que separada que pueda implementarse como método static en ambas activities.
        loadMyLastLocation(googleMap);
        loadAllUsersLocations(googleMap);

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps2, container, false);
        setupPermissionsLocation();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void loadAllUsersLocations (GoogleMap googleMap){
        //Realizamos conexión con servicios web para verificar usuario y contraseña
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        String url = "http://192.168.0.15/simple-web-service/app/list.php";

        //Creamos el cuerpo de la Request, incluimos el método POST, la url a la que llamamos y pasamos parámetros JSON.
        //Luego asignamos acciones en caso de respones válida o error.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response ->{
                    try {
                        JSONArray agents = response.getJSONArray("temp");
                        for (int i = 0; i < agents.length(); i++){
                            //Creamos el intent heredando valores de la activity anterior, y un nuevo bundle con los datos.
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();

                            //Guardando datos en bundle
                            bundle.putString("nombre",agents.getJSONObject(i).getString("nombre"));
                            bundle.putString("apellidos",agents.getJSONObject(i).getString("apellidos"));
                            bundle.putString("ciudad",agents.getJSONObject(i).getString("ciudad"));
                            bundle.putString("provincia",agents.getJSONObject(i).getString("provincia"));
                            bundle.putString("temperatura",agents.getJSONObject(i).getString("temperatura"));
                            bundle.putInt("format",agents.getJSONObject(i).getInt("format"));

                            String name = agents.getJSONObject(i).getString("nombre") + " "+ agents.getJSONObject(i).getString("apellidos");
                            String address = agents.getJSONObject(i).getString("ciudad") + " " + agents.getJSONObject(i).getString("provincia");

                            LatLng userLocation = this.getLatLng(address);

                            googleMap.addMarker(new MarkerOptions().position(userLocation).title(name));
                            //TODO al pinchar sobre una etiqueta que lleve a la ficha de paciente. De momento se guarda solo el último registro.
//                            googleMap.setOnInfoWindowClickListener(marker -> {
//                                startActivity(new Intent(getContext(), TempResultsActivity.class).putExtras(bundle));
//                            });
                        }
                    } catch (JSONException e) {
                        Log.d("COMPROBACION", "Error en carga de datos: " + e.getMessage());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Log.d("COMPROBACION", "onErrorResponse: " + error.getMessage());
                }
        );
        //Añadimos la request a la cola.
        queue.add(jsonObjectRequest);
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

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        //Cogemos la última localización.
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
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

    public LatLng getLatLng(String location) throws IOException {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = geocoder.getFromLocationName(location,1);
        double latitude = addressList.get(0).getLatitude();
        double longitude = addressList.get(0).getLongitude();

        return new LatLng(latitude, longitude);
    }
}