package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.adapter.DonasiAdapter;
import com.example.kumpul_dana.model.UserDonation;
import com.example.kumpul_dana.database.DatabaseHelper;
import java.util.List;
import java.util.ArrayList;

public class RiwayatDonasiActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRiwayatDonasi;
    private DonasiAdapter donasiAdapter;
    private List<UserDonation> riwayatDonasiList;
    private DatabaseHelper dbHelper;
    private int currentLoggedInUserId;
    private ImageView backButtonRiwayat;
    private TextView textViewNoDonations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_donasi);

        recyclerViewRiwayatDonasi = findViewById(R.id.recyclerViewRiwayatDonasi);
        recyclerViewRiwayatDonasi.setLayoutManager(new LinearLayoutManager(this));

        backButtonRiwayat = findViewById(R.id.backButtonRiwayat);
        textViewNoDonations = findViewById(R.id.textViewNoDonations);

        dbHelper = new DatabaseHelper(this);

        // Ambil ID pengguna yang sedang login dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentLoggedInUserId = prefs.getInt("user_id", -1);

        // Listener untuk tombol kembali
        backButtonRiwayat.setOnClickListener(v -> finish());

        loadDonationHistory(); // Panggil metode untuk memuat riwayat donasi
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDonationHistory(); // Muat ulang data setiap kali Activity kembali ke foreground
    }

    private void loadDonationHistory() {
        // Periksa apakah user ID ditemukan
        if (currentLoggedInUserId != -1) {
            riwayatDonasiList = dbHelper.getDonationsByUserIdWithProjectTitle(currentLoggedInUserId);

            if (riwayatDonasiList.isEmpty()) {
                textViewNoDonations.setVisibility(View.VISIBLE);
                recyclerViewRiwayatDonasi.setVisibility(View.GONE);
                Toast.makeText(this, "Anda belum memiliki riwayat donasi.", Toast.LENGTH_SHORT).show();
            } else {
                textViewNoDonations.setVisibility(View.GONE);
                recyclerViewRiwayatDonasi.setVisibility(View.VISIBLE);
                if (donasiAdapter != null) {
                    donasiAdapter.updateData(riwayatDonasiList);
                } else {
                    donasiAdapter = new DonasiAdapter(this, riwayatDonasiList);
                    recyclerViewRiwayatDonasi.setAdapter(donasiAdapter);
                }
            }
        } else {
            // Handle jika pengguna belum login atau sesi berakhir
            Toast.makeText(this, "Sesi Anda berakhir. Mohon login kembali.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(RiwayatDonasiActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}