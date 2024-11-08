package com.example.languageappprototype;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Country.class}, version = 1)
public abstract class CountryDatabase extends RoomDatabase {
    private static volatile CountryDatabase INSTANCE;

    public abstract CountryDao countryDao();

    public static CountryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CountryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CountryDatabase.class, "country_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
