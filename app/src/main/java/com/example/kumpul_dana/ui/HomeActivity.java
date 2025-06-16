package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import java.text.NumberFormat;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView textViewHaiUser;
    private TextView textViewSaldo;
    private View saldoClickableArea;
    private Button buttonLogout; // Mengganti nama variabel agar lebih jelas
    private Button buttonIsiFormDonasi;
    private Button buttonRiwayatDonasi;

    private DatabaseHelper dbHelper;
    private int currentLoggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi elemen-elemen UI
        textViewHaiUser = findViewById(R.id.textViewHaiUser);
        textViewSaldo = findViewById(R.id.textViewSaldo);
        saldoClickableArea = findViewById(R.id.saldoClickableArea);
        // Pastikan ID di activity_home.xml adalah 'buttonLogoutUser'
        buttonLogout = findViewById(R.id.buttonLogoutUser);
        buttonIsiFormDonasi = findViewById(R.id.buttonIsiFormDonasi);
        buttonRiwayatDonasi = findViewById(R.id.buttonRiwayatDonasi);

        // --- Mendapatkan ID pengguna dari SharedPreferences ---
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        currentLoggedInUserId = prefs.getInt("user_id", -1); // Ambil ID pengguna

        // --- Set Listener untuk Tombol-Tombol User ---

        // === FUNGSI LOGOUT DITAMBAHKAN DI SINI ===
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hapus data sesi dari SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("user_session", MODE_PRIVATE).edit();
                editor.remove("user_id"); // Hapus ID pengguna yang tersimpan
                editor.putBoolean("isLoggedIn", false); // Tandai sebagai sudah logout
                editor.apply();

                // Tampilkan pesan logout berhasil
                Toast.makeText(HomeActivity.this, "Anda berhasil logout", Toast.LENGTH_SHORT).show();

                // Arahkan kembali ke LoginActivity
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                // Flags ini untuk membersihkan semua activity sebelumnya dari stack,
                // sehingga pengguna tidak bisa kembali ke HomeActivity dengan menekan tombol back.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Tutup HomeActivity
                finish();
            }
        });

        buttonIsiFormDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoggedInUserId != -1) {
                    Intent intent = new Intent(HomeActivity.this, PilihProyekActivity.class);
                    intent.putExtra("USER_ID", currentLoggedInUserId);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Mohon login untuk mengisi form donasi.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonRiwayatDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoggedInUserId != -1) {
                    Intent intent = new Intent(HomeActivity.this, RiwayatDonasiActivity.class);
                    intent.putExtra("USER_ID", currentLoggedInUserId);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Mohon login untuk melihat riwayat donasi.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saldoClickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoggedInUserId != -1) {
                    Intent intent = new Intent(HomeActivity.this, TopUpSaldoActivity.class);
                    intent.putExtra("USER_ID", currentLoggedInUserId);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Mohon login untuk mengisi saldo.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Panggil updateUI() untuk pertama kali saat activity dibuat
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(); // Memperbarui UI setiap kali activity aktif kembali
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

            double userBalance = dbHelper.getUserBalance(currentLoggedInUserId);
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
            String formattedBalance = formatRupiah.format(userBalance).replace(",00", "");
            textViewSaldo.setText("Rp " + formattedBalance); // Sedikit merapikan format
        } else {
            // Jika tidak ada user ID, langsung arahkan ke login
            Toast.makeText(this, "Sesi Anda berakhir. Mohon login kembali.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
    }
}
