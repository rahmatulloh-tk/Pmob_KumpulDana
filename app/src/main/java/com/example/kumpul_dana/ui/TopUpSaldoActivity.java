package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem; // Import untuk onOptionsItemSelected
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText; // Menggunakan ini karena di XML pakai TextInputEditText

import java.text.NumberFormat;
import java.util.Locale;

public class TopUpSaldoActivity extends AppCompatActivity {

    private TextInputEditText editTextAmount; // Ganti ke TextInputEditText
    private TextInputEditText editTextPassword; // NEW: Untuk password
    private Spinner spinnerPaymentMethod; // NEW: Untuk metode pembayaran
    private Button buttonTopUp;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_saldo);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Sembunyikan title default
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Tampilkan tombol kembali
        }

        // Inisialisasi DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ambil User ID dari Intent
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID tidak ditemukan. Mohon login kembali.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inisialisasi UI komponen
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextPassword = findViewById(R.id.editTextPassword); // NEW
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod); // NEW
        buttonTopUp = findViewById(R.id.buttonTopUp);
        progressBar = findViewById(R.id.progressBar);

        // Setup Spinner untuk Metode Pembayaran
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.payment_methods_array, // Array string yang sudah kamu buat di strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        buttonTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptTopUp();
            }
        });
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

    private void attemptTopUp() {
        // Reset error
        editTextAmount.setError(null);
        editTextPassword.setError(null);
        // spinnerPaymentMethod tidak punya setError, bisa gunakan Toast atau visual lain

        String amountStr = editTextAmount.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

        boolean cancel = false;
        View focusView = null;

        // Validasi input Nominal
        if (TextUtils.isEmpty(amountStr)) {
            editTextAmount.setError("Nominal tidak boleh kosong");
            focusView = editTextAmount;
            cancel = true;
        } else {
            double amount = 0;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    editTextAmount.setError("Nominal harus lebih dari 0");
                    focusView = editTextAmount;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                editTextAmount.setError("Nominal tidak valid");
                focusView = editTextAmount;
                cancel = true;
            }
        }

        // Validasi Metode Pembayaran
        if (paymentMethod.equals("Pilih metode") || TextUtils.isEmpty(paymentMethod)) {
            Toast.makeText(this, "Mohon pilih metode pembayaran.", Toast.LENGTH_SHORT).show();
            // Tidak ada focusView untuk Spinner, jadi kita bisa langsung set cancel = true jika perlu
            cancel = true;
        }

        // Validasi Password
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password tidak boleh kosong");
            focusView = editTextPassword;
            cancel = true;
        }

        if (cancel) {
            // Ada error, fokus ke view yang salah
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Semua validasi awal lolos, lanjutkan dengan konfirmasi
            double amount = Double.parseDouble(amountStr); // Pasti bisa parse karena sudah divalidasi
            showConfirmationDialog(amount, password);
        }
    }

    private void showConfirmationDialog(double amount, String password) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedAmount = formatRupiah.format(amount).replace(",00", "");
        String selectedPaymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Pengisian Saldo")
                .setMessage("Nominal: " + formattedAmount +
                        "\nMetode Pembayaran: " + selectedPaymentMethod +
                        "\n\nApakah Anda yakin ingin melanjutkan?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Verifikasi password sebelum eksekusi top-up
                        verifyPasswordAndExecuteTopUp(amount, password);
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void verifyPasswordAndExecuteTopUp(double amount, String password) {
        // Ambil username user yang sedang login dari database
        String username = dbHelper.getUsername(currentUserId);
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Gagal mendapatkan data user. Mohon login kembali.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Lakukan verifikasi password. Metode ini ada di DatabaseHelper
        // Diasumsikan DatabaseHelper memiliki metode untuk memverifikasi password
        // Contoh: public boolean checkUserPassword(int userId, String password)
        boolean isPasswordCorrect = dbHelper.checkUserPassword(currentUserId, password);

        if (isPasswordCorrect) {
            executeTopUp(amount); // Lanjutkan top-up jika password benar
        } else {
            Toast.makeText(this, "Password salah. Pengisian saldo dibatalkan.", Toast.LENGTH_LONG).show();
            // Opsional:editTextPassword.setError("Password salah");
            // Opsional: editTextPassword.requestFocus();
        }
    }


    private void executeTopUp(double amount) {
        // Tampilkan loading bar
        progressBar.setVisibility(View.VISIBLE);
        buttonTopUp.setEnabled(false); // Nonaktifkan tombol saat loading
        setFormEnabled(false); // Nonaktifkan input form

        // Simulasikan proses loading (misal: 2 detik)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean success = dbHelper.addBalance(currentUserId, amount);

                // Sembunyikan loading bar
                progressBar.setVisibility(View.GONE);
                buttonTopUp.setEnabled(true); // Aktifkan kembali tombol
                setFormEnabled(true); // Aktifkan input form

                if (success) {
                    Toast.makeText(TopUpSaldoActivity.this, "Saldo sebesar " +
                            NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(amount).replace(",00", "") +
                            " berhasil ditambahkan!", Toast.LENGTH_LONG).show();
                    // Kembali ke HomeActivity dan pastikan saldo di sana terupdate
                    // HomeActivity akan mengupdate saldonya di onResume
                    finish();
                } else {
                    Toast.makeText(TopUpSaldoActivity.this, "Gagal menambahkan saldo. Silakan coba lagi.", Toast.LENGTH_LONG).show();
                }
            }
        }, 2000); // Durasi loading 2 detik
    }

    // Metode untuk mengaktifkan/menonaktifkan input form
    private void setFormEnabled(boolean enabled) {
        editTextAmount.setEnabled(enabled);
        editTextPassword.setEnabled(enabled);
        spinnerPaymentMethod.setEnabled(enabled);
    }
}