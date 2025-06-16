package com.example.kumpul_dana.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem; // Import untuk Toolbar
import android.widget.Toast;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.example.kumpul_dana.model.UserDonation;
import com.example.kumpul_dana.adapter.AdminDonationAdapter; // NEW: Adapter baru

import java.util.List;
import java.util.ArrayList;

public class AdminDonationHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDonations;
    private AdminDonationAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<UserDonation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donation_history);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Sembunyikan title default
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Tampilkan tombol kembali
        }

        dbHelper = new DatabaseHelper(this);
        recyclerViewDonations = findViewById(R.id.recyclerViewAdminDonations);
        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(this));

        loadDonationData();
    }

    // Handle tombol kembali di Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Kembali ke activity sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDonationData() {
        donationList = dbHelper.getAllDonationsWithProjectAndUserInfo(); // Ambil semua donasi
        if (donationList.isEmpty()) {
            Toast.makeText(this, "Belum ada donasi yang tercatat.", Toast.LENGTH_SHORT).show();
        }

        adapter = new AdminDonationAdapter(this, donationList);
        recyclerViewDonations.setAdapter(adapter);
    }
}