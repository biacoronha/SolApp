package com.example.android.sunshine.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {WeatherEntry.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class SolAppDatabase extends RoomDatabase {

    private static final String LOG_TAG = SolAppDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "weather";
    private static final Object LOCK = new Object();
    private static SolAppDatabase sInstance;

    public static SolAppDatabase getInstance(Context context) {

        Log.d(LOG_TAG, "Getting the database");

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        SolAppDatabase.class, SolAppDatabase.DATABASE_NAME).build();
                Log.d(LOG_TAG, "Made new database");
            }
        }
        return sInstance;

    }

    public abstract WeatherDao weatherDao();
}