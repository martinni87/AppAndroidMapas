package com.example.primera_aplicacion;

//import static com.example.primera_aplicacion.common.Constants.DB_NAME;

import static com.example.primera_aplicacion.common.Constants.LOGIN_PASS;
import static com.example.primera_aplicacion.common.Constants.LOGIN_USER;
import static com.example.primera_aplicacion.common.Constants.SP_PREFERENCES_DIRECTORY;
import static com.example.primera_aplicacion.common.Constants.SP_USERS_KEY;
import static com.example.primera_aplicacion.common.Constants.SP_VALIDATION_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
//    //Variable de control de logging
    private boolean loginCheck;
    // Variables de objetos en activity layout
    private Button btnEntrar; //Almacenará el botón ENTRAR
    private EditText etUser, etPassword; //Almacenará datos de campos usuario y contraseña
    private CheckBox rememberUser; //Almacenará la información sobre si se recuerda o no el usuario
//    private AppDatabase database; //Una vez creados los models y el dao, instanciamos un objeto de AppDatabase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Asignamos a las variables creadas un objeto del layout
        btnEntrar = findViewById(R.id.btnEntrar);
        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        rememberUser = findViewById(R.id.checkBox);

        //Si al cargar tenemos guardado en sharedpreferences, que las credenciales son válidas, etonces mantenemos
        //sesión abierta y pasamos directamente a siguiente activity.
        // Si hay datos guardados en las sharedpreferences, los cargamos
        loadSharedPreferences();

        //Si ya tenemos la validación realizada previamente y guardada en el sharedpreferences, vamos directamente a la siguiente activity.
        if (loginCheck){
            goToNextActivity();
        }

        //Si dicha validación no se ha realizado correctamente aún, o se ha cerrado sesión, se podrá hacer con el botón del login.
        // Asignamos una acción de click al botón
        btnEntrar.setOnClickListener(doLogin);

        //Creamos la instancia que controla la database
//        database = Room.databaseBuilder(LoginActivity.this, AppDatabase.class,DB_NAME)
                //.allowMainThreadQueries() //Esto permite la ejecución de hilos de segundo plano en primer plano... NO HACERLO ESTO ES SOLO PRUEBA
                //.fallbackToDestructiveMigration() //Si hay un cambio en la base de datos, se borra lo que hay. Esto solo está bien usarlo en pruebas.
                //En lugar de allowMainThreadQueries vamos a usar consultas asíncronas en hilos secundarios
                //En kotlin son corrutinas y en java tenemos RxJava o LiveData y Guava
//                .build();
    }

    //La acción del botón ENTRAR viene definida por el siguiente método
    private final View.OnClickListener doLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //Asignamos valor a variables locales para facilitar la lectura del código.
            String user = String.valueOf(etUser.getText());
            String password = etPassword.getText() + "";
            boolean remember = rememberUser.isChecked();

            /**
             * Si se ha marcado recordar usuario, entonces almacenamos el valor introducido en el textview de usuario
             * en nuestro sharedpreferences. Si no se marca, su valor será false, y por tanto se almacena null (nada).
             * El método también registra el estado del checkbox, si se marca, se guarda su valor como true.
             */
            saveUserOnSP(remember?user:null);

            //Método que guarda el intento de inicio de sesión en la base de datos
//            saveInDatabase(user,password,remember);

            // Si el método areCredencialsValid devuelve true, se crea el Intent y se lanza la siguiente Activity
            if (areCredencialsValid(user, password)) {
                saveStateCredentials(remember);
                goToNextActivity();
            }
        }
    };

    // Método validaciones, pasamos como parámetros un String para el usuario y otro para la contraseña
    private boolean areCredencialsValid(String user, String password) {
        //Al inicio seteamos errores a null, para que desaparezcan cuando el usuario inicia por primera vez, o hace una corrección.
        etUser.setError(null);
        etPassword.setError(null);

        //Verificación 1, debe rellenarse todos los campos
        if ("".equalsIgnoreCase(user) && "".equalsIgnoreCase(password)) {
            //Si no se han rellenado, se marcan los dos con error (circulo lateral)
            etUser.setError("Usuario obligatorio");
            etPassword.setError("Contraseña obligatoria");
            //Al fallar mandamos el foco del cursor al primer dato (users) y retornamos false en la verificación
            etUser.requestFocus();
            return false;
        }

        //Verificación 2, si no se cumple la condición anterior (porque uno de los dos se ha rellenado) vemos cuál es el vacío
        if ("".equalsIgnoreCase(user)) {
            //Si el usuario no se ha rellenado, se marca con error, se hace foco y retornamos false
            etUser.setError("Usuario obligatorio");
            etUser.requestFocus();
            return false;
        }
        if ("".equalsIgnoreCase(password)){
            //Si el usuario se ha rellenado pero no el password, se marca passs con error, se hace foco y retornamos false
            etPassword.setError("Contraseña obligatoria"); //Idem
            etPassword.requestFocus();
            return false;
        }

// VALIDACIONES ANTIGUAS. DEPRECATED
        //Verificación 2 el usuario debe ser admin
        if (!LOGIN_USER.equalsIgnoreCase(user)) {
            etUser.setError("Usuario incorrecto");
            etUser.requestFocus();
            return false; //Al fallar retorna false
        }
        //Verificación 3 la contraseña debe ser admin
        if (!LOGIN_PASS.equalsIgnoreCase(password)) {
            etPassword.setError("Contraseña incorrecta");
            etPassword.requestFocus();
            return false; //Al fallar retorna false
        }
        //Si ninguna de las condiciones anteriores se cumple, entonces el resultado es true y se valida el Login
//        loginCheck = true;
        return true;
    }

    //Método que crea directorio para shared preferences y lo devuelve para usarse en otros métodos
    private SharedPreferences loadSharedPreferencesDirectory (){
        /*
        Con MODE_PRIVATE como parámetro del método getSharedPreferences evitamos que otras apps puedan hacer uso
        de lo que se guarde en sharedpreferences.
        SP_PREFERENCES_DIRECTORY = savedOptions es el nombre que recibe "el fichero" donde se almacenará la información.
        Obtenemos info almacenada, solo modo lectura
        */
        return getApplicationContext().getSharedPreferences(SP_PREFERENCES_DIRECTORY,MODE_PRIVATE);
    }
    //Método para guardar el valor del campo usuario en un sharedpreference
    private void saveUserOnSP(String user){
        //Cargamos el directorio de sharedpreferences
        SharedPreferences spDirectory = loadSharedPreferencesDirectory();
        //Creamos un editor de los sharedpreferences
        SharedPreferences.Editor spEditor = spDirectory.edit();

        //Para guardar el nombre de usuario usamos un putString, recibe clave -> Valor. Hay que terminar la transacción con un commit o apply
        spEditor.putString(SP_USERS_KEY,user);
        spEditor.apply(); //Si en lugar de apply usa
    }

    //Método para guardar el valor de credenciales validas true o false.
    private void saveStateCredentials(Boolean validation){
        SharedPreferences spDirectory = loadSharedPreferencesDirectory();
        SharedPreferences.Editor spEditor = spDirectory.edit();
        spEditor.putBoolean(SP_VALIDATION_KEY, validation);
        spEditor.apply();
    }

    //Método para recuperar el valor del campo usuario cuando se haya guardado en sharedpreferences
    private void loadSharedPreferences(){
        //Cargamos el directorio de sharedpreferences
        SharedPreferences spDirectory = loadSharedPreferencesDirectory();

        //Asignamos el valor de la clave USER al string, valor por defecto null (si no se ha guardado nada).
        String user = spDirectory.getString(SP_USERS_KEY,null);

        //Asignamos el valor resultante de la comprobación de las credenciales
        boolean validation = spDirectory.getBoolean(SP_VALIDATION_KEY,false);

        //Seteamos si el checkbox debe ir marcado o no. La condición (user != null) dará falso si está vacío (unchecked) y true si tiene contenido (checked)
        rememberUser.setChecked(user != null);
        etUser.setText(user);

        //Finalmente asignamos a la variable global loginCheck el valor guardado en SP_VALIDATION_KEY
        loginCheck = validation;
    }

    /**
     * Método para ir a la siguiente activity
     */
    private void goToNextActivity(){
        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivity(intent);
    }

//    private boolean checkValidationHTTP(String user, String password){
//        //Variable que almacenará el estado de la validación de credenciales
//        JSONObject[] agents = new JSONObject[1];
//        boolean validation[] = {false};
////        boolean validateCredentials[] = {false};
//        //Realizamos conexión con servicios web para verificar usuario y contraseña
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        String url = "http://192.168.0.15/simple-web-service/app/auth.php";
//
//        //Creamos un conjunto CLAVE => VALOR que contendrá los datos del JSON que vamos a enviar.
//        HashMap<String,String> parameters = new HashMap<>();
//        parameters.put("user",user);
//        parameters.put("passwd",password);
//
//        //Codificamos el conjunto como JSONObject
//        JSONObject jsonDataParameter = new JSONObject(parameters);
//
//        //Creamos el cuerpo de la Request, incluimos el método POST, la url a la que llamamos y pasamos parámetros JSON.
//        //Luego asignamos acciones en caso de respones válida o error.
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.POST,
//                url,
//                jsonDataParameter,
//                response ->{
////                    Log.d(HTTP_REQUEST_TAG, "response: " + response);
//                    //Obtenemos el JsonArray
//                    agents[0] = response;
//                    try {
//                        Log.d(HTTP_REQUEST_TAG, "Success: " + agents[0].getBoolean("success"));
//                        validation[0] = agents[0].getBoolean("success");
////                        validateCredentials[0] = agents[0].getBoolean("success");
////                        Log.d(HTTP_REQUEST_TAG, "checkValidationHTTP: " + validateCredentials[0]);
//                    } catch (JSONException e) {
//                        Log.d(HTTP_REQUEST_TAG, "Success error: " + e.getMessage());
//                    }
//                },
//                error -> {
//                    Log.d(HTTP_REQUEST_TAG, "onErrorResponse: " + error.getMessage());
//                }
//        );
//
//        //Añadimos la request a la cola.
//        queue.add(jsonObjectRequest);
//
//        if (!validation[0]){
//            etUser.setError("Credenciales incorrectas");
//            etPassword.setError("Credenciales incorrectas");
//            etUser.requestFocus();
//            return false;
//        }
//
//        return true;
//    }

    //Método para guardar intentos de inicio de sesión en la base de datos
//    private void saveInDatabase(String user, String password, boolean remember){
//        //Dao al que queremos acceder
//        LoginDao loginDao = database.loginDao();
//
//        //Login
//        Login login = new Login();
////        login.id = (int) Math.round(Math.random() * (9999 - 0 + 1)); //El id no es autoincremental, por lo que tenemos que asignarlo cada vez
//        login.user = user;
//        login.password = password;
//        login.remember = remember;

        //Insertamos en la DB el dato del intento de acceso
//        loginDao.insertData(login);

        //Vemos el insert hecho en el Logcat en debug
//        Single<List<Login>> logins = loginDao.getAll();
//        Log.d("Mis pruebas",String.valueOf(logins));
//    }
}