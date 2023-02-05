package com.example.primera_aplicacion.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.primera_aplicacion.common.Constants.SP_PREFERENCES_DIRECTORY;
import static com.example.primera_aplicacion.common.Constants.SP_SETTINGS_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.primera_aplicacion.NavigationActivity;
import com.example.primera_aplicacion.R;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    private Switch celsius, fahrenheit;
    private Button btnGuardar;
    private int format; //El valor de format será globalmente en la app 1 si es celsius, 2 si es fahrenheit

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);

        celsius = rootView.findViewById(R.id.switchCelsius);
        fahrenheit = rootView.findViewById(R.id.switchFahrenheit);
        btnGuardar = rootView.findViewById(R.id.btnGuardar);

        //Cargamos información de sharedpreferences para saber qué settings se habían guardado.
        format = loadFromSP();

        //Actualizamos la vista de los botones celsius y fahrenheit según la info de sharedpreferences
        celsius.setChecked(format == 1);
        fahrenheit.setChecked(format == 2);

        //Asignamos listeners
        celsius.setOnCheckedChangeListener(checkOnOffFahrenheit);
        fahrenheit.setOnCheckedChangeListener(checkOnOffCelsius);
        btnGuardar.setOnClickListener(clickOnGuardar);

        return rootView;
    }

    protected CompoundButton.OnCheckedChangeListener checkOnOffFahrenheit = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            fahrenheit.setChecked(!celsius.isChecked());
            format = !celsius.isChecked() ? 2 : 1;
        }
    };

    protected CompoundButton.OnCheckedChangeListener checkOnOffCelsius = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            celsius.setChecked(!fahrenheit.isChecked());
            format = !fahrenheit.isChecked() ? 1 : 2;
        }
    };

    protected View.OnClickListener clickOnGuardar = view -> {
        Intent intent = new Intent(getContext(), NavigationActivity.class);
        saveToSP(format);
        Toast.makeText(getContext(),"Configuración guardada",Toast.LENGTH_SHORT).show();
        startActivity(intent);
    };

    /**
     * SHARED PREFERENCES METHOD para guardar la configuración del settings. Según lo que se marque como predefinido aquí
     * al acceder al TempFragment, se tendrá seleccionado bien Celsius o Fahrenheit por defecto.
     */
    private void saveToSP(int format){
        //Cargamos las sharedpreference guardadas
        /**
         * Con MODE_PRIVATE como parámetro del método getSharedPreferences evitamos que otras apps puedan hacer uso
         * de lo que se guarde en sharedpreferences.
         * savedOptions es el nombre que recibe "el fichero" donde se almacenará la información.
         */
        //Obtenemos info almacenada, solo modo lectura
        SharedPreferences spSaved = Objects.requireNonNull(getContext()).getSharedPreferences(SP_PREFERENCES_DIRECTORY,MODE_PRIVATE);

        //Creamos un editor de los sharedpreferences
        SharedPreferences.Editor spEditor = spSaved.edit();

        //Para guardar el nombre de usuario usamos un putString, recibe clave -> Valor. Hay que terminar la transacción con un commit o apply
        spEditor.putInt(SP_SETTINGS_KEY,format);
        spEditor.apply(); //Si en lugar de apply usa
    }
    private int loadFromSP(){
        //Cargamos las sharedpreferences guardadas.
        SharedPreferences spSaved = getContext().getSharedPreferences(SP_PREFERENCES_DIRECTORY, MODE_PRIVATE);

        //Asignamos el valor de la clave USER al string, valor por defecto null (si no se ha guardado nada).
        int format = spSaved.getInt(SP_SETTINGS_KEY,1);

        return format;
    }
}