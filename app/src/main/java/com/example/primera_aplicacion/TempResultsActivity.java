package com.example.primera_aplicacion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedHashMap;
import java.util.Map;

public class TempResultsActivity extends AppCompatActivity {

    //LinkedHashMap para almacenar datos en camposFormulario ordenados según orden de inserción.
    private LinkedHashMap<String, EditText> camposFormulario = new LinkedHashMap<>();
    //Variables que almacenan el recuadro de color con mensaje del resultado y el botón MENU
    private TextView colorResultado;
    private Button btnVolverMenu;
    //Variables que utilizamos para cálculos más abajo.
    private double temperatureValue;
    private String escala;
    private int format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_results);

        //Asignamos campos del formulario al Map
        camposFormulario.put("nombre",(EditText) findViewById(R.id.etNombre));
        camposFormulario.put("apellidos",(EditText) findViewById(R.id.etApellidos));
        camposFormulario.put("temperatura",(EditText) findViewById(R.id.etTemperatura));
        camposFormulario.put("ciudad",(EditText) findViewById(R.id.etCiudad));
        camposFormulario.put("provincia", (EditText) findViewById(R.id.etProvincia));

        //Asignamos el TextView que servirá de cuadro de resultado.
        colorResultado = findViewById(R.id.view_color_resultado);

        //Asignamos el botón MENU
        btnVolverMenu = findViewById(R.id.btnVolverMenu);

        //Creamos el intent heredando valores de la activity anterior, y un nuevo bundle con los datos.
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        //Usamos un bucle foreach para recorrer el Map y asignarle los valores correspondientes
        for (Map.Entry<String,EditText> entry:camposFormulario.entrySet()){
            entry.getValue().setText(bundle.getString(entry.getKey()));
        }

        //Tomamos el valor de la temperatura en formato String y lo convertimos a Double
        //Previamente nos hemos asegurado que el usuario solo pueda introducir numeros en el campo
        //Temperatura, por lo que no podrá generar un error al transformar el String a Double.
        String temp = String.valueOf(camposFormulario.get("temperatura").getText());
        temperatureValue = Double.parseDouble(temp);

        //Tomamos el valor de radioButton seleccionado para seleccionar la escala
        format = bundle.getInt("format");
        escala = (format == 1) ? "celsius" : "fahrenheit";
//        escala = bundle.getString("escala");

        //Con los datos de la temperatura llamamos al método testTemperatura
        testTemperatura(temperatureValue,escala);

        //Asignamos el Listener al botón menu.
        btnVolverMenu.setOnClickListener(clickOnVolverMenu);

        //Añadimos método para guardar datos pasados por intent en base de datos. Usamos los web services
//        saveToDataBase();
    }

    protected View.OnClickListener clickOnVolverMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Al hacer click en MENU volveremos a la OptionsActivity
            Intent intent = new Intent(TempResultsActivity.this,NavigationActivity.class);
            startActivity(intent);
        }
    };

    public void testTemperatura(double value, String escala){
        //Analizamos en cada caso si la temperatura se encuentra en un rango concreto para una escala concreta
        //Cada situación pinta el recuadro de un color y escribe un mensaje dentro.
        //Si se cumple el primer supuesto, no tendrá fiebre
        if ((value < 37 && escala.equalsIgnoreCase("celsius"))
                || (value < 98.6 && escala.equalsIgnoreCase("fahrenheit"))){
            colorResultado.setText("Temperatura normal");
            colorResultado.setBackgroundColor(Color.GREEN);
        }
        //Si se cumple el segundo supuesto, tendrá febrícula
        else if ((value < 38 && escala.equalsIgnoreCase("celsius"))
                || (value < 100.4 && escala.equalsIgnoreCase("fahrenheit"))){
            colorResultado.setText("Precaución. Febrícula");
            colorResultado.setBackgroundColor(Color.rgb(253,132,31));
        }
        //Si se cumple el último supuesto, tendrá fiebre.
        else{
            colorResultado.setTextColor(Color.WHITE);
            colorResultado.setText("¡Atención! Fiebre");
            colorResultado.setBackgroundColor(Color.RED); //En este caso cambiamos también el color de texto para mejor visualización
        }
    }

    /**
     * Método saveToDataBase registra una nueva entrada en la base de datos.
     * Toma los valores pasados por intent previamente a la activity y los transforma a un objeto JSON
     * para mandarlos al servicio web en PHP que se encargará de registrarlo en la base de datos.
     */
//    private void saveToDataBase (){
//        //Lista en donde almacenaremos los usuarios recogidos de la base de datos
//        HashMap<Integer, User> userList = new HashMap<>();
//
//        //Realizamos conexión con servicios web para verificar usuario y contraseña
//        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
//        String url = "http://192.168.0.15/simple-web-service/app/temp.php";
//
//        //Creamos un conjunto CLAVE => VALOR que contendrá los datos del JSON que vamos a enviar.
//        HashMap<String,String> parameters = new HashMap<>();
//        parameters.put("nombre",camposFormulario.get("nombre").getText().toString());
//        parameters.put("apellidos",camposFormulario.get("apellidos").getText().toString());
//        parameters.put("temperatura",camposFormulario.get("temperatura").getText().toString());
//        parameters.put("format",format+""); //El valor pasado en un futuro debe ser 1 si es medida en Celsius y 2 si es Fahrenheit
//        parameters.put("ciudad",camposFormulario.get("ciudad").getText().toString());
//        parameters.put("provincia",camposFormulario.get("provincia").getText().toString());
////        Log.d("martin", "saveToDataBase: " + camposFormulario.get("nombre").getText().toString());
//
//        //Codificamos el conjunto como JSONObject
//        JSONObject jsonDataParameter = new JSONObject(parameters);
//
//        //Creamos el cuerpo de la Request, incluimos el método POST, la url a la que llamamos y pasamos parámetros JSON.
//        //Luego asignamos acciones en caso de respones válida o error.
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.POST, url, jsonDataParameter,
//                response ->{
//                    try {
//                        String msg = response.getBoolean("success") ?
//                            "Se ha registrado la información en la BD" :
//                            "Hubo un fallo en el registro con la BD";
//
//                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
////                        Log.d("martin", "saveToDataBase: " + response.getBoolean("success"));
//
//                    } catch (JSONException e) {
//                        Log.d("COMPROBACION", "Error en carga de datos: " + e.getMessage());
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