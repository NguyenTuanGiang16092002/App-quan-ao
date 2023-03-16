package com.example.footballfashion.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.footballfashion.model.Sport;

import java.util.List;

@Dao
public interface ClothDAO {

    @Insert
    void insertCloth(Sport Sport);

    @Query("SELECT * FROM Sports")
    List<Sport> getListClothCart();

    @Query("SELECT * FROM Sports WHERE id=:id")
    List<Sport> checkClothInCart(int id);

    @Delete
    void deleteCloth(Sport Sport);

    @Update
    void updateCloth(Sport Sport);

    @Query("DELETE from Sports")
    void deleteAllCloth();
}
