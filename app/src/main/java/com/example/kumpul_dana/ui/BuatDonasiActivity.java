package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuatDonasiActivity extends AppCompatActivity {

    private TextView textViewProjectTitle;
    private TextInputEditText editTextNama;
    private TextInputEditText editTextNominal;
    private Spinner spinnerMetodePembayaran;
    // private TextInputEditText editTextPesan; // Hapus ini
    private Button buttonSelanjutnya;
    private ImageView backButton;

    private DatabaseHelper dbHelper;
    private int currentUserId;
    private int currentProjectId;
    private String projectTitle;
    private double targetAmount;
    private double collectedAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_donasi);

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi elemen-elemen UI
        textViewProjectTitle = findViewById(R.id.textViewProjectTitle);
        editTextNama = findViewById(R.id.editTextNama); // Ini mungkin perlu disesuaikan jika tidak ada nama pengirim
        editTextNominal = findViewById(R.id.editTextNominal);
        spinnerMetodePembayaran = findViewById(R.id.spinnerMetodePembayaran);
        // editTextPesan = findViewById(R.id.editTextPesan); // Hapus inisialisasi ini
        buttonSelanjutnya = findViewById(R.id.buttonSelanjutnya);
        backButton = findViewById(R.id.backButton);

        // Ambil data dari Intent
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        currentProjectId = getIntent().getIntExtra("PROJECT_ID", -1);
        projectTitle = getIntent().getStringExtra("PROJECT_TITLE");
        targetAmount = getIntent().getDoubleExtra("TARGET_AMOUNT", 0.0);
        collectedAmount = getIntent().getDoubleExtra("COLLECTED_AMOUNT", 0.0);

        if (projectTitle != null) {
            textViewProjectTitle.setText(projectTitle);
        }

        // Setup Spinner untuk Metode Pembayaran
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodePembayaran.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());

        buttonSelanjutnya.setOnClickListener(v -> {
            String nominalStr = editTextNominal.getText().toString().trim();
            String metodePembayaran = spinnerMetodePembayaran.getSelectedItem().toString();
            // String pesanDonasi = editTextPesan.getText().toString().trim(); // Hapus ini

            if (nominalStr.isEmpty()) {
                Toast.makeText(BuatDonasiActivity.this, "Nominal harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (metodePembayaran.equals("Pilih Metode Pembayaran")) {
                Toast.makeText(BuatDonasiActivity.this, "Pilih metode pembayaran!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(nominalStr);

                if (amount <= 0) {
                    Toast.makeText(BuatDonasiActivity.this, "Nominal donasi harus lebih dari 0.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lanjutkan ke Activity berikutnya (PembayaranQrisActivity atau UploadBuktiActivity)
                // Jika PembayaranQrisActivity hanya untuk tampilan QRIS, maka data donasi baru disubmit di UploadBuktiActivity
                Intent intent = new Intent(BuatDonasiActivity.this, UploadBuktiActivity.class); // Ubah ke UploadBuktiActivity
                intent.putExtra("USER_ID", currentUserId);
                intent.putExtra("PROJECT_ID", currentProjectId);
                intent.putExtra("AMOUNT", amount);
                intent.putExtra("PAYMENT_METHOD", metodePembayaran);
                // intent.putExtra("DONATION_MESSAGE", pesanDonasi); // Hapus ini

                startActivity(intent);

            } catch (NumberFormatException e) {
                Toast.makeText(BuatDonasiActivity.this, "Nominal tidak valid!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}