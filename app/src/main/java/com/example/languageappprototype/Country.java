package com.example.languageappprototype;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "country_table")
public class Country {
    @PrimaryKey
    public int no;
    public String country;
    public String language;


    public Country(){

    }
    // Constructor
    public Country(int no, String country, String language) {
        this.no = no;
        this.country = country;
        this.language = language;
    }
}
