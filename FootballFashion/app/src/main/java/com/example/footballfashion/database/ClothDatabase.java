package com.example.footballfashion.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.footballfashion.model.Sport;

@Database(entities = {Sport.class}, version = 1)
public abstract class ClothDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "football_fashion.db";

    private static ClothDatabase instance;

    public static synchronized ClothDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ClothDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract ClothDAO clothDAO();
}
