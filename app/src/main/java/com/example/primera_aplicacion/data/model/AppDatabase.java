//package com.example.primera_aplicacion.data.model;
//
//import androidx.room.AutoMigration;
//import androidx.room.Database;
//import androidx.room.RoomDatabase;
//
//import com.example.primera_aplicacion.data.dao.LoginDao;
//
//@Database(
//        entities = {Login.class},
//        version = 4,
//        //Para cosas simples utilizaremos automigrations, recibe 3 parámetros, from, to y el objeto que hace la migración (opcional)
//        autoMigrations = {
//                @AutoMigration(from = 3, to = 4)    //Al hacer cambios en la BD, tenemos que aumentar el número de versión
//                                            // e indicar el tipo de automigración entre versiones.
//})
//public abstract class AppDatabase extends RoomDatabase {
//    //Creamos un método por cada uno de los Daos que tenemos.
//    public abstract LoginDao loginDao();
//}
