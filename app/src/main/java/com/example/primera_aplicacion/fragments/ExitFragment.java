package com.example.primera_aplicacion.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.primera_aplicacion.common.Constants.SP_PREFERENCES_DIRECTORY;
import static com.example.primera_aplicacion.common.Constants.SP_VALIDATION_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.primera_aplicacion.LoginActivity;
import com.example.primera_aplicacion.MapsActivity;
import com.example.primera_aplicacion.R;

public class ExitFragment extends Fragment {
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exit, container, false);
        saveStateCredentials(false);
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        return rootView;
    }

    private void saveStateCredentials(Boolean validation){
        SharedPreferences spDirectory = getContext().getSharedPreferences(SP_PREFERENCES_DIRECTORY,MODE_PRIVATE);
        SharedPreferences.Editor spEditor = spDirectory.edit();
        spEditor.putBoolean(SP_VALIDATION_KEY, validation);
        spEditor.apply();
    }
}