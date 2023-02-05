package com.example.primera_aplicacion.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.primera_aplicacion.common.Constants.SP_PREFERENCES_DIRECTORY;
import static com.example.primera_aplicacion.common.Constants.SP_SETTINGS_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.primera_aplicacion.R;
import com.example.primera_aplicacion.TempResultsActivity;
import com.example.primera_aplicacion.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class TempFragment extends Fragment {
    //Declaramos btnFinalizar para el botón del formulario
    private Button btnFinalizar;
    //camposFormulario almacena con un bucle foreach los datos de entrada de cada campo del formulario
    private LinkedHashMap<String, EditText> camposFormulario = new LinkedHashMap<>();
    //datosPaciente recoge la información de camposFormulario en formato String para pasarlo como bundle a la siguiente Activity
    private LinkedHashMap<String, String> datosPaciente = new LinkedHashMap<>();
    //Variables que almacenarán el objeto radioButton seleccionado en la vista.
    private RadioButton rbCelsius, rbFahrenheit;
    private int format; //Se añade para adaptar la app al formato de la BD. Valor 1 indica celsius, valor 2 indica fahrenheit

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_temperatura, container, false);

        //Cargamos datos guardados de sharedpreferences
        format = loadFromSP();

        //Asignamos el objeto botón
        btnFinalizar = rootView.findViewById(R.id.btnFinalizarTemp);

        //Con la llamada al método put del Map, asignamos pares Clave:Valor, en este caso String:EditText
        camposFormulario.put("nombre", rootView.findViewById(R.id.etNombre));
        camposFormulario.put("apellidos", rootView.findViewById(R.id.etApellidos));
        camposFormulario.put("temperatura", rootView.findViewById(R.id.etTemperatura));
        camposFormulario.put("ciudad", rootView.findViewById(R.id.etCiudad));
        camposFormulario.put("provincia", rootView.findViewById(R.id.etProvincia));

        //Asignamos los objetos RadioButton
        rbCelsius = rootView.findViewById(R.id.rbCelsius);
        rbFahrenheit = rootView.findViewById(R.id.rbFahrenheit);

        //Seteamos si el checkbox debe ir marcado o no. La condición (user != null) dará falso si está vacío (unchecked) y true si tiene contenido (checked)
        rbCelsius.setChecked(format == 1);
        rbFahrenheit.setChecked(format == 2);

        //Creamos el listener y llamamos al método clickOnFinalizar cuando se presione el botón finalizar
        btnFinalizar.setOnClickListener(clickOnFinalizar);

        return rootView;
    }

    //Método clickOnFinalizar
    protected View.OnClickListener clickOnFinalizar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Sentencia if con validación. Entramos si el método validacionOK devuelve true (todos los campos rellenos)
            if (validacionOK()){
                //putStringValuesIntoList() es un método que usa un foreach para recorrer camposFormulario
                //extraer con getValue los valores Strings asociados a cada clave y asignarlos a datosPaciente
                putStringValuesIntoList();
                //Creamos el bundle y el intent para configurar el cambio de activity
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getContext(), TempResultsActivity.class);

                //Añadimos cada conjunto CLAVE:VALOR de datosPaciente al bundle
                for (Map.Entry<String,String> entry:datosPaciente.entrySet()){
                    bundle.putString(entry.getKey(), entry.getValue());
                }

                //Nos queda asignar el tipo de escala, que lo hacemos con el operador ternario, según esté marcado celsius o no
                //Si ninguno está marcado (Celsius o Fahrenheit) nunca llegamos aquí, porque habría fallado la validación
                //por lo que en este punto sí o sí estará marcado Celsius o Fahrenheit
                format = (rbCelsius.isChecked() ? 1 : 2); //1 Celsius, 2 Fahrenheit
//                String escala = (rbCelsius.isChecked() ? "Celsius" : "Fahrenheit");

                //Añadimos la escala al bundle
                bundle.putInt("format",format);
//                bundle.putString("escala",escala);

                //Cargamos el bundle en el intent y pasamos a la siguiente activity
                intent.putExtras(bundle);

                //Una vez validado, añadimos método para guardar datos pasados por intent en base de datos. Usamos los web services
                saveToDataBase();

                //Lanzamos la nueva activity
                startActivity(intent);
            }
        }
    };

    private boolean validacionOK(){
        //Seteamos cada campo con setError null al inicio y cuando un usuario vaya a editar un campo para corregir.
        camposFormulario.get("nombre").setError(null);
        camposFormulario.get("apellidos").setError(null);
        camposFormulario.get("temperatura").setError(null);
        camposFormulario.get("ciudad").setError(null);
        camposFormulario.get("provincia").setError(null);
        rbCelsius.setError(null);
        rbFahrenheit.setError(null);

        //Recorremos camposFormulario y si algún campo está vacío, saltará el error y no permitirá continuar.
        for (Map.Entry<String,EditText> entry:camposFormulario.entrySet()) {
            if ("".equalsIgnoreCase(entry.getValue().getText()+"")){
                entry.getValue().requestFocus();
                entry.getValue().setError("Es obligatorio rellenar todos los campos");
                return false; //Devolvemos false si alguno falla y se sale del bucle y del método
            }
        }
        //Comprobados los campos, verificamos que se haya marcado o bien rbCelsius o rbFahrenheit
        if (!rbCelsius.isChecked() && !rbFahrenheit.isChecked()){
            rbCelsius.requestFocus();
            //El mensaje de error lo damos en forma de Toast, por simplificar el ejercicio.
            //Para un setError("mensaje") sería necesario separar la comprobación de ambos RadioButton
            Toast.makeText(getContext(), "Seleccionar unidad de medida", Toast.LENGTH_SHORT).show();
            return false;//Devolvemos false si alguno falla y se sale del bucle y del método
        }
        return true;//Devolvemos true si todas las comprobaciones son correctas
    }

    private void putStringValuesIntoList(){
        // Bucle for each que recorre campos formulario y asigna cada conjunto clave valor a datos paciente
        //extrayendo de los objetos EditText el valor String almacenado.
        for (Map.Entry<String,EditText> entry:camposFormulario.entrySet()) {
            datosPaciente.put(entry.getKey(), entry.getValue().getText()+"");
        }
    }

    /**
     * Método saveToDataBase registra una nueva entrada en la base de datos.
     * Toma los valores pasados por intent previamente a la activity y los transforma a un objeto JSON
     * para mandarlos al servicio web en PHP que se encargará de registrarlo en la base de datos.
     */
    private void saveToDataBase(){
        //Lista en donde almacenaremos los usuarios recogidos de la base de datos
        HashMap<Integer, User> userList = new HashMap<>();

        //Realizamos conexión con servicios web para verificar usuario y contraseña
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        String url = "http://192.168.0.15/simple-web-service/app/temp.php";

        //Creamos un conjunto CLAVE => VALOR que contendrá los datos del JSON que vamos a enviar.
        HashMap<String,String> parameters = new HashMap<>();
        parameters.put("nombre",camposFormulario.get("nombre").getText().toString());
        parameters.put("apellidos",camposFormulario.get("apellidos").getText().toString());
        parameters.put("temperatura",camposFormulario.get("temperatura").getText().toString());
        parameters.put("format",format+""); //El valor pasado en un futuro debe ser 1 si es medida en Celsius y 2 si es Fahrenheit
        parameters.put("ciudad",camposFormulario.get("ciudad").getText().toString());
        parameters.put("provincia",camposFormulario.get("provincia").getText().toString());
//        Log.d("martin", "saveToDataBase: " + camposFormulario.get("nombre").getText().toString());

        //Codificamos el conjunto como JSONObject
        JSONObject jsonDataParameter = new JSONObject(parameters);

        //Creamos el cuerpo de la Request, incluimos el método POST, la url a la que llamamos y pasamos parámetros JSON.
        //Luego asignamos acciones en caso de respones válida o error.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonDataParameter,
                response ->{
                    try {
                        String msg = response.getBoolean("success") ?
                                "Se ha registrado la información en la BD" :
                                "Hubo un fallo en el registro con la BD";
                        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
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
    private int loadFromSP(){
        //Cargamos las sharedpreferences guardadas.
        SharedPreferences spSaved = getContext().getSharedPreferences(SP_PREFERENCES_DIRECTORY, MODE_PRIVATE);

        //Asignamos el valor de la clave USER al string, valor por defecto null (si no se ha guardado nada).
        int format = spSaved.getInt(SP_SETTINGS_KEY,1);

        return format;
    }
}