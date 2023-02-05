package com.example.primera_aplicacion.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.primera_aplicacion.MapsActivity;
import com.example.primera_aplicacion.R;
import com.example.primera_aplicacion.models.User;
import com.example.primera_aplicacion.models.UsersAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        loadFromDataBase(rootView);
        return rootView;
    }

    private void loadFromDataBase (View rootView){
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
                        ArrayList<User> data = new ArrayList<>();
                        for (int i = 0; i < agents.length(); i++){
                            data.add(new User(
                                    agents.getJSONObject(i).getString("nombre"),
                                    agents.getJSONObject(i).getString("apellidos"),
                                    agents.getJSONObject(i).getString("ciudad"),
                                    agents.getJSONObject(i).getString("provincia"),
                                    agents.getJSONObject(i).getInt("temperatura"),
                                    agents.getJSONObject(i).getInt("format")));
                        }
                        UsersAdapter adapter = new UsersAdapter(getContext(),data);
                        ListView listView = rootView.findViewById(R.id.lvLista);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(itemHasBeenSelected);
                    } catch (JSONException e) {
                        Log.d("COMPROBACION", "Error en carga de datos: " + e.getMessage());
                    }
                },
                error -> {
                    Log.d("COMPROBACION", "onErrorResponse: " + error.getMessage());
                }
        );
        //Añadimos la request a la cola.
        queue.add(jsonObjectRequest);
    }



    protected AdapterView.OnItemClickListener itemHasBeenSelected = (parent, view, position, id) -> {
        //Creamos el bundle y el intent para configurar el cambio de activity
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getContext(), MapsActivity.class);

        //Añadimos cada conjunto CLAVE:VALOR de datosPaciente al bundle
        bundle.putString("nombre", ((User) parent.getItemAtPosition(position)).getName());
        bundle.putString("apellidos", ((User) parent.getItemAtPosition(position)).getLastname());
        bundle.putString("ciudad", ((User) parent.getItemAtPosition(position)).getCity());
        bundle.putString("provincia", ((User) parent.getItemAtPosition(position)).getProvince());
        bundle.putString("temperatura", ((User) parent.getItemAtPosition(position)).getTemperature()+"");
        bundle.putInt("format", ((User) parent.getItemAtPosition(position)).getFormat());

//        //Cargamos el bundle en el intent y pasamos a la siguiente activity
        intent.putExtras(bundle);
        startActivity(intent);
    };
}