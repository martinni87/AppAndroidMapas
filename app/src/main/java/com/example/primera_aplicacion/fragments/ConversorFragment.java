package com.example.primera_aplicacion.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.primera_aplicacion.R;

public class ConversorFragment extends Fragment {
    Button btnCalcular1, btnCalcular2;
    EditText etCelsius2Convert, etFahrenheit2Convert, celsiusConverted2Fah, fahConverted2Celsius;

    public ConversorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Creamos una vista y la inflamos para este fragment asignando el layout de la vista
        View rootView = inflater.inflate(R.layout.activity_conversor, container, false);

        etCelsius2Convert = rootView.findViewById(R.id.et_celsius_value2convert);
        etFahrenheit2Convert = rootView.findViewById(R.id.et_fahrenheit_value2convert);
        celsiusConverted2Fah = rootView.findViewById(R.id.et_celsius_converted2Fahrenheit);
        fahConverted2Celsius = rootView.findViewById(R.id.et_fahrenheit_converted2Celsius);

        btnCalcular1 = rootView.findViewById(R.id.btnCalcular1);
        btnCalcular2 = rootView.findViewById(R.id.btnCalcular2);

        btnCalcular1.setOnClickListener(view -> {
            String valueString = String.valueOf(etCelsius2Convert.getText());
            float value = Float.parseFloat(valueString);
            float converted = (value * 9/5) + 32;
            String convertedString = String.format("%.01f",converted);
            celsiusConverted2Fah.setText(convertedString);
        });

        btnCalcular2.setOnClickListener(view -> {
            String valueString = String.valueOf(etFahrenheit2Convert.getText());
            float value = Float.parseFloat(valueString);
            float converted = (value - 32) * 5/9;
            String convertedString = String.format("%.01f",converted);
            fahConverted2Celsius.setText(convertedString);
        });

        return rootView;
    }
}