package com.example.primera_aplicacion.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
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
import com.example.primera_aplicacion.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class MapsFragment extends Fragment {

    MapsActivity myMaps = new MapsActivity();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        myMaps.setupPermissionsLocation();

    }

    private OnMapReadyCallback callback = googleMap -> {
        myMaps.loadMyLastLocation(googleMap);
//        loadAllUsersLocations(googleMap);
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_maps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

//    private void loadAllUsersLocations(GoogleMap googleMap){
//        //Realizamos conexión con servicios web para verificar usuario y contraseña
//        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
//        String url = "http://192.168.0.15/simple-web-service/app/list.php";
//
//        //Creamos el cuerpo de la Request, incluimos el método POST, la url a la que llamamos y pasamos parámetros JSON.
//        //Luego asignamos acciones en caso de respones válida o error.
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET, url, null,
//                response ->{
//                    try {
//                        JSONArray agents = response.getJSONArray("temp");
//                        ArrayList<User> data = new ArrayList<>();
//
//                        for (int i = 0; i < agents.length(); i++){
//                            //Cargamos la información de localización de cada usario para mostrarla en el mapa
//                            String name = agents.getJSONObject(i).getString("nombre") + " " +
//                                    agents.getJSONObject(i).getString("apellidos");
//                            String address = agents.getJSONObject(i).getString("ciudad") + " " +
//                                    agents.getJSONObject(i).getString("provincia");
//                            LatLng userLocation = myMaps.getLatLng(address);
//                            googleMap.addMarker(new MarkerOptions().position(userLocation).title(name));
//                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
//
//                            //Para cada usuario, recogemos todos los datos y los guardamos en un bundle
//                            Bundle bundle = new Bundle();
//                            Intent intent = new Intent(getContext(), TempResultsActivity.class);
//
//                            //Añadimos cada conjunto CLAVE:VALOR de datosPaciente al bundle
//                            bundle.putString("nombre", agents.getJSONObject(i).getString("nombre"));
//                            bundle.putString("apellidos", agents.getJSONObject(i).getString("apellidos"));
//                            bundle.putString("ciudad", agents.getJSONObject(i).getString("ciudad"));
//                            bundle.putString("provincia", agents.getJSONObject(i).getString("provincia"));
//                            bundle.putString("temperatura", agents.getJSONObject(i).getString("temperatura"));
//                            bundle.putInt("format", agents.getJSONObject(i).getInt("format"));
//                            intent.putExtras(bundle);
//                            googleMap.setOnInfoWindowClickListener(marker -> {
//                                startActivity(new Intent(getContext(), TempResultsActivity.class).putExtras(bundle));
//                            });
////        //Cargamos el bundle en el intent y pasamos a la siguiente activity
////
////                            startActivity(intent);
////                            data.add(new User(
////                                    agents.getJSONObject(i).getString("nombre"),
////                                    agents.getJSONObject(i).getString("apellidos"),
////                                    agents.getJSONObject(i).getString("ciudad"),
////                                    agents.getJSONObject(i).getString("provincia"),
////                                    agents.getJSONObject(i).getInt("temperatura"),
////                                    agents.getJSONObject(i).getInt("format")));
//
//                        }
//                    } catch (JSONException e) {
//                        Log.d("COMPROBACION", "Error en carga de datos: " + e.getMessage());
//                    } catch (IOException e) {
//                        Log.d("COMPROBACION", "Error en carga de credenciales de localización: " + e.getMessage());
//                    }
//                },
//                error -> {
//                    Log.d("COMPROBACION", "onErrorResponse: " + error.getMessage());
//                }
//        );
//        //Añadimos la request a la cola.
//        queue.add(jsonObjectRequest);
//    }

}