//package com.example.primera_aplicacion.data.model;
//
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;
//
//// A nivel de negocio esto guarda intentos de inicio de sesión.
//// A nivel de BD, esto representa un registro (1 fila).
//@Entity
//public class Login {
//
//    // Cada variable es una columna de la tabla de BD.
//    @PrimaryKey(autoGenerate = true) //Asignamos primary key auto-incremental
//    public int id;
////    @ColumnInfo(name = "usuario") //Podemos referenciar la variable user en Java a una columna Usuario en SQL.
//    public String user;
//// Si no usamos el tag @ColumnInfo, el nombre de la variable debe coincidir con su homologo en SQL.
//    public String password;
////    @ColumnInfo(name = "recuerda")
//    public boolean remember;
//
//    @Override
//    public String toString(){
//        return "Login{" +
//                "id = " + id +
//                ", user = " + user +
//                ", password = " + password +
//                ", remember = " + remember +
//                "}\n";
//    }
//}
