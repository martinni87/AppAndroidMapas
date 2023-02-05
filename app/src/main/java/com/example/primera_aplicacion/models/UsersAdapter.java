package com.example.primera_aplicacion.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.primera_aplicacion.R;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> {
    ArrayList<User> data;

    public UsersAdapter(@NonNull Context context, @NonNull ArrayList<User> usersArray){
        super (context, R.layout.users, usersArray);
        data = usersArray;
    }

    static class ViewHolder{
        TextView tvName;
        TextView tvLocation;
        TextView tvNumber;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ViewHolder viewHolder = new ViewHolder();
        View item = convertView;
        if (item == null){
            LayoutInflater inflate = LayoutInflater.from(getContext());
            item = inflate.inflate(R.layout.users, null);
            viewHolder.tvName = item.findViewById(R.id.tvName);
            viewHolder.tvLocation = item.findViewById(R.id.tvLocation);
            viewHolder.tvNumber = item.findViewById(R.id.tvNumber);
            item.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) item.getTag();
        }

        viewHolder.tvName.setText(data.get(position).getName() + " " + data.get(position).getLastname());
        viewHolder.tvLocation.setText(data.get(position).getCity() + ", " + data.get(position).getProvince());
        viewHolder.tvNumber.setText(data.get(position).getTemperature()+"º " + ((data.get(position).getFormat()==1)?"C":"F"));

        return item;
    }
}
