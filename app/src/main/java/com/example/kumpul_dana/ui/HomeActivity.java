package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kumpul_dana.adapter.ProjectAdapter;
import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.example.kumpul_dana.ui.PilihProyekActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView textViewHaiUser;
    private TextView textViewDonasiCount; // Untuk "4 Donasi"
    private Button buttonEditProfil; // Tombol "Edit profil" di header
    private Button buttonIsiFormDonasi; // Tombol "ISI FORM DONASI"
    private Button buttonRiwayatDonasi; // Tombol "LIHAT RIWAYAT DONASI"

    private DatabaseHelper dbHelper;
    private int currentLoggedInUserId; // Variabel untuk menyimpan ID pengguna yang sedang login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi elemen-elemen UI
        textViewHaiUser = findViewById(R.id.textViewHaiUser);
        textViewDonasiCount = findViewById(R.id.textViewDonasiCount);
        buttonEditProfil = findViewById(R.id.buttonEditProfil);
        buttonIsiFormDonasi = findViewById(R.id.buttonIsiFormDonasi);
        buttonRiwayatDonasi = findViewById(R.id.buttonRiwayatDonasi);


        // --- Mendapatkan ID pengguna dari SharedPreferences ---
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentLoggedInUserId = prefs.getInt("user_id", -1); // Ambil ID pengguna

        // Mengambil dan menampilkan username berdasarkan ID yang didapat
        if (currentLoggedInUserId != -1) {
            String username = dbHelper.getUsername(currentLoggedInUserId); // Ambil username dari database
            if (username != null && !username.isEmpty()) {
                textViewHaiUser.setText("Hai, " + username + "!"); // Contoh: "Hai, Mutiara Shinta"
            } else {
                textViewHaiUser.setText("Hai, Pengguna!"); // Fallback jika username kosong/null
            }
            // Mengambil dan menampilkan jumlah donasi aktual
            int totalDonasi = dbHelper.getTotalDonationsForUser(currentLoggedInUserId); // Memanggil fungsi baru di DatabaseHelper
            textViewDonasiCount.setText(totalDonasi + " Donasi");
        } else {
            // Jika tidak ada user_id di SharedPreferences (contoh: belum login atau sesi habis)
            textViewHaiUser.setText("Hai, Tamu!");
            textViewDonasiCount.setText("0 Donasi");
            Toast.makeText(this, "Sesi Anda berakhir. Mohon login kembali.", Toast.LENGTH_LONG).show();
            // Opsional: Langsung arahkan ke LoginActivity
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Tutup HomeActivity
        }

        // --- Set Listener untuk Tombol-Tombol User ---

        buttonEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoggedInUserId != -1) {
                    Toast.makeText(HomeActivity.this, "Fitur Edit Profil belum dibuat", Toast.LENGTH_SHORT).show();
                    // Intent intent = new Intent(HomeActivity.this, EditProfilActivity.class);
                    // intent.putExtra("USER_ID", currentLoggedInUserId); // Jika EditProfil butuh USER_ID
                    // startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Mohon login untuk mengedit profil.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonIsiFormDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoggedInUserId != -1) { // Pastikan user sudah login
                    Intent intent = new Intent(HomeActivity.this, PilihProyekActivity.class);
                    intent.putExtra("USER_ID", currentLoggedInUserId); // <<< PENTING: Meneruskan USER_ID
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Mohon login untuk mengisi form donasi.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonRiwayatDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoggedInUserId != -1) { // Pastikan user sudah login
                    Intent intent = new Intent(HomeActivity.this, RiwayatDonasiActivity.class);
                    intent.putExtra("USER_ID", currentLoggedInUserId); // <<< PENTING: Meneruskan USER_ID
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Mohon login untuk melihat riwayat donasi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Metode ini akan dipanggil setiap kali activity kembali ke foreground
    @Override
    protected void onResume() {
        super.onResume();
        // Perbarui tampilan jika ada perubahan data (misal setelah donasi baru)
        updateUI();
    }

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentLoggedInUserId = prefs.getInt("user_id", -1);

        if (currentLoggedInUserId != -1) {
            String username = dbHelper.getUsername(currentLoggedInUserId);
            if (username != null && !username.isEmpty()) {
                textViewHaiUser.setText("Hai, " + username + "!");
            } else {
                textViewHaiUser.setText("Hai, Pengguna!");
            }
            int totalDonasi = dbHelper.getTotalDonationsForUser(currentLoggedInUserId);
            textViewDonasiCount.setText(totalDonasi + " Donasi");
        } else {
            textViewHaiUser.setText("Hai, Tamu!");
            textViewDonasiCount.setText("0 Donasi");
            // Tidak perlu Toast atau redirect di onResume, karena bisa mengganggu
        }
    }
}