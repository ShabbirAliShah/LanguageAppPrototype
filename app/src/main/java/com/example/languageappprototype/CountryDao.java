package com.example.languageappprototype;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CountryDao {
    @Query("SELECT * FROM country_table")
    List<Country> getAllCountries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Country> countries);

    @Query("DELETE FROM country_table")
    void deleteAll();
}
