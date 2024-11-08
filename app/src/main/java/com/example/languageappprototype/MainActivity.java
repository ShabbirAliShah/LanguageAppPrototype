package com.example.languageappprototype;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private CountryAdapter adapter;
    private CountryDatabase db;
    private DatabaseReference firebaseRef;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new CountryAdapter();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = CountryDatabase.getDatabase(this);
        firebaseRef = FirebaseDatabase.getInstance().getReference("languageData");

        findViewById(R.id.btnView).setOnClickListener(v -> viewData());
        findViewById(R.id.btnSync).setOnClickListener(v -> syncData());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteAllData());
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void syncData() {
        if (isInternetConnected()) {
            firebaseRef.get().addOnSuccessListener(dataSnapshot -> {
                List<Country> countries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Country country = snapshot.getValue(Country.class);
                    if (country != null) {
                        countries.add(country);
                    }
                }
                // Insert data on a background thread
                executor.execute(() -> {
                    db.countryDao().insertAll(countries);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Data synced successfully.", Toast.LENGTH_SHORT).show();
                        viewData(); // Display the newly fetched data
                    });
                });
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to fetch data.", Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(this, "Please connect to the internet first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewData() {
        executor.execute(() -> {
            List<Country> countries = db.countryDao().getAllCountries();
            runOnUiThread(() -> {
                if (countries.isEmpty()) {
                    Toast.makeText(this, "Database is empty. Please sync data first.", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.setCountryList(countries);
                }
            });
        });
    }

    private void deleteAllData() {
        executor.execute(() -> {
            List<Country> countries = db.countryDao().getAllCountries();
            runOnUiThread(() -> {
                if (countries.isEmpty()) {
                    Toast.makeText(this, "Database is already empty.", Toast.LENGTH_SHORT).show();
                } else {
                    executor.execute(() -> {
                        db.countryDao().deleteAll();
                        runOnUiThread(() -> {
                            adapter.setCountryList(new ArrayList<>());
                            Toast.makeText(this, "All data deleted from local database.", Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            });
        });
    }
}