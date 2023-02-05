package com.example.primera_aplicacion.models;

import android.util.Log;

public class User {
    private String name;
    private String lastname;
    private String city;
    private String province;
    private int temperature;
    private int format;

    public User(String name, String lastname, String location, String province, int temperature, int format){
        this.name = name;
        this.lastname = lastname;
        this.city = location;
        this.province = province;
        this.temperature = temperature;
        this.format = format;
    }

    public String getName(){
        return name;
    }
    public String getLastname(){
        return lastname;
    }
    public String getCity(){
        return city;
    }
    public String getProvince(){
        return province;
    }
    public int getTemperature(){
        return temperature;
    }
    public int getFormat(){
        return format;
    }

    public void printData(){
        Log.d("Object User", "Data: " +
                getName() + " - " + getLastname() + " - " +
                getCity() + " - " + getProvince() + " - " +
                getTemperature() + " - " + getFormat());
    }
}
