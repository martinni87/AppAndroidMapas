//package com.example.primera_aplicacion.data.dao;
//
//// Esto es la tabla que representa los métodos que usará el resto de la app para interactuar con los
//// datos de la tabla login. Aquí definimos los métodos que hacen las queries.
//
//import androidx.room.Dao;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.example.primera_aplicacion.data.model.Login;
//
//import java.util.List;
//
//import io.reactivex.rxjava3.core.Completable;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.Single;
//
//@Dao
//public interface LoginDao {
//
//    @Query("SELECT * FROM login")
////    List<Login> getAll();
//    public Single<List<Login>> getAll();
//
//    @Insert (onConflict = OnConflictStrategy.REPLACE)
////    void insert(Login login);
//    public Completable insertData(Login login);
//}
