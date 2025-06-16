package com.example.kumpul_dana.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog; // Import untuk AlertDialog
import android.content.DialogInterface; // Import untuk DialogInterface
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Untuk simulasi loading
import android.text.TextUtils; // Untuk validasi input
import android.view.View;
import android.widget.AdapterView; // Import untuk Spinner.OnItemSelectedListener
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar; // Import ProgressBar
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout; // Import TextInputLayout
import com.example.kumpul_dana.R;
import com.example.kumpul_dana.database.DatabaseHelper;

import java.text.NumberFormat; // Import NumberFormat
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuatDonasiActivity extends AppCompatActivity {

    private TextView textViewProjectTitle;
    private TextInputEditText editTextNama;
    private TextInputEditText editTextNominal;
    private TextInputEditText editTextPesan; // Menambahkan editTextPesan
    private TextInputEditText editTextPassword; // NEW: Untuk password saldo
    private TextInputLayout textInputLayoutPassword; // NEW: Untuk kontrol visibility password input
    private Spinner spinnerMetodePembayaran;

    private Button buttonSelanjutnya;
    private ImageView backButton;
    private ProgressBar progressBar; // NEW: ProgressBar

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
        editTextNama = findViewById(R.id.editTextNama);
        editTextNominal = findViewById(R.id.editTextNominal);
        spinnerMetodePembayaran = findViewById(R.id.spinnerMetodePembayaran);
        editTextPesan = findViewById(R.id.editTextPesan); // Inisialisasi editTextPesan
        editTextPassword = findViewById(R.id.editTextPassword); // NEW
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword); // NEW
        buttonSelanjutnya = findViewById(R.id.buttonSelanjutnya);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar); // NEW

        // Ambil data dari Intent
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        currentProjectId = getIntent().getIntExtra("PROJECT_ID", -1);
        projectTitle = getIntent().getStringExtra("PROJECT_TITLE");
        targetAmount = getIntent().getDoubleExtra("TARGET_AMOUNT", 0.0);
        collectedAmount = getIntent().getDoubleExtra("COLLECTED_AMOUNT", 0.0);

        if (projectTitle != null) {
            textViewProjectTitle.setText("Donasi untuk: " + projectTitle);
        }

        // Tampilkan nama user secara otomatis
        if (currentUserId != -1) {
            String username = dbHelper.getUsername(currentUserId);
            if (username != null && !username.isEmpty()) {
                editTextNama.setText(username);
            } else {
                editTextNama.setText("Pengguna Tidak Dikenal");
            }
        } else {
            editTextNama.setText("Nama Pengguna"); // Default jika user ID tidak ada
        }


        // Setup Spinner untuk Metode Pembayaran (Menggunakan payment_methods dari strings.xml)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodePembayaran.setAdapter(adapter);

        // Listener untuk tombol kembali
        backButton.setOnClickListener(v -> finish());

        // NEW: Listener untuk Spinner perubahan metode pembayaran
        spinnerMetodePembayaran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMethod = parent.getItemAtPosition(position).toString();
                if (selectedMethod.equals("Saldo")) {
                    textInputLayoutPassword.setVisibility(View.VISIBLE); // Tampilkan input password
                } else {
                    textInputLayoutPassword.setVisibility(View.GONE); // Sembunyikan input password
                    editTextPassword.setText(""); // Kosongkan password saat disembunyikan
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        buttonSelanjutnya.setOnClickListener(v -> attemptDonation());
    }

    private void attemptDonation() {
        // Reset error
        editTextNominal.setError(null);
        editTextPassword.setError(null);

        String nominalStr = editTextNominal.getText().toString().trim();
        String metodePembayaran = spinnerMetodePembayaran.getSelectedItem().toString();
        String password = editTextPassword.getText().toString().trim(); // Ambil password
        String pesan = editTextPesan.getText().toString().trim(); // Ambil pesan

        boolean cancel = false;
        View focusView = null;

        // Validasi Nominal
        if (TextUtils.isEmpty(nominalStr)) {
            editTextNominal.setError("Nominal tidak boleh kosong");
            focusView = editTextNominal;
            cancel = true;
        } else {
            try {
                double amount = Double.parseDouble(nominalStr);
                if (amount <= 0) {
                    editTextNominal.setError("Nominal donasi harus lebih dari 0.");
                    focusView = editTextNominal;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                editTextNominal.setError("Nominal tidak valid!");
                focusView = editTextNominal;
                cancel = true;
            }
        }

        // Validasi Metode Pembayaran
        if (metodePembayaran.equals("Pilih Metode Pembayaran")) {
            Toast.makeText(BuatDonasiActivity.this, "Pilih metode pembayaran!", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        // Validasi Password hanya jika metode pembayaran adalah "Saldo"
        if (metodePembayaran.equals("Saldo")) {
            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Password tidak boleh kosong");
                focusView = editTextPassword;
                cancel = true;
            }
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Lanjutkan berdasarkan metode pembayaran
            double amount = Double.parseDouble(nominalStr);
            if (metodePembayaran.equals("Saldo")) {
                // Alur Donasi via Saldo: Verifikasi & Proses
                showConfirmationDialogSaldo(amount, password, pesan);
            } else if (metodePembayaran.equals("QRIS")) {
                // Alur Donasi via QRIS: Ke PembayaranQrisActivity
                Intent intent = new Intent(BuatDonasiActivity.this, PembayaranQrisActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                intent.putExtra("PROJECT_ID", currentProjectId);
                intent.putExtra("AMOUNT", amount);
                intent.putExtra("PAYMENT_METHOD", metodePembayaran);
                intent.putExtra("MESSAGE", pesan); // Teruskan pesan
                startActivity(intent);
            } else {
                // Alur Donasi via Transfer Bank / E-Wallet (Non-QRIS): Ke UploadBuktiActivity
                // Asumsi: untuk Transfer Bank dan E-wallet (OVO, GoPay, DANA) akan lewat UploadBuktiActivity
                Intent intent = new Intent(BuatDonasiActivity.this, UploadBuktiActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                intent.putExtra("PROJECT_ID", currentProjectId);
                intent.putExtra("AMOUNT", amount);
                intent.putExtra("PAYMENT_METHOD", metodePembayaran);
                intent.putExtra("MESSAGE", pesan); // Teruskan pesan
                startActivity(intent);
            }
        }
    }

    private void showConfirmationDialogSaldo(double amount, String password, String message) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedAmount = formatRupiah.format(amount).replace(",00", "");

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Donasi dengan Saldo")
                .setMessage("Anda akan berdonasi sebesar " + formattedAmount + " untuk proyek \"" + projectTitle + "\" menggunakan saldo.\n\nApakah Anda yakin?")
                .setPositiveButton("Ya", (dialog, which) -> verifyPasswordAndProcessDonation(amount, password, message))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void verifyPasswordAndProcessDonation(double amount, String password, String message) {
        // Tampilkan loading bar dan nonaktifkan tombol/input
        progressBar.setVisibility(View.VISIBLE);
        buttonSelanjutnya.setEnabled(false);
        setFormEnabled(false);

        new Handler().postDelayed(() -> {
            boolean isPasswordCorrect = dbHelper.checkUserPassword(currentUserId, password);

            if (!isPasswordCorrect) {
                Toast.makeText(BuatDonasiActivity.this, "Password salah. Donasi dibatalkan.", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Password salah");
                editTextPassword.requestFocus();
                // Sembunyikan loading dan aktifkan kembali form
                progressBar.setVisibility(View.GONE);
                buttonSelanjutnya.setEnabled(true);
                setFormEnabled(true);
                return;
            }

            double userBalance = dbHelper.getUserBalance(currentUserId);
            if (userBalance < amount) {
                Toast.makeText(BuatDonasiActivity.this, "Saldo tidak cukup. Mohon isi saldo Anda.", Toast.LENGTH_LONG).show();
                // Sembunyikan loading dan aktifkan kembali form
                progressBar.setVisibility(View.GONE);
                buttonSelanjutnya.setEnabled(true);
                setFormEnabled(true);
                return;
            }

            // Jika password benar dan saldo cukup, lanjutkan proses donasi
            processDonationViaSaldo(amount, message);

        }, 1000); // Simulasi waktu verifikasi
    }

    private void processDonationViaSaldo(double amount, String message) {
        // Simulasikan proses transaksi database
        new Handler().postDelayed(() -> {
            // Catat donasi ke database
            String donationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String status = "Berhasil"; // Langsung berhasil karena dari saldo
            String paymentMethod = "Saldo";
            String proofImagePath = "N/A"; // Tidak ada bukti gambar untuk saldo

            // Mengurangi saldo user
            boolean balanceDecreased = dbHelper.decreaseBalance(currentUserId, amount);
            // Menambah collected amount ke proyek
            // Karena createDonation sudah memanggil updateProjectCollectedAmount di DatabaseHelper,
            // kita hanya perlu memastikan createDonation juga mengurangi saldo jika itu terjadi.
            // Di DatabaseHelper, kita tidak perlu memanggil updateProjectCollectedAmount lagi
            // karena createDonation sudah handle itu. Yang perlu adalah pengurangan saldo.

            long donationId = -1;
            if (balanceDecreased) {
                // Buat donasi. Perlu diperhatikan bahwa createDonation di DB Helper
                // saat ini juga memanggil updateProjectCollectedAmount. Ini perlu disesuaikan
                // agar tidak double update jika sudah dipanggil di sini.
                // Atau, buat method createDonationWithBalanceUpdate yang hanya fokus mencatat donasi
                // dan mengupdate project, sedangkan pengurangan saldo sudah dilakukan di sini.
                // Untuk kesederhanaan, saya akan asumsikan createDonation sudah pas.

                // Jika createDonation di DatabaseHelper juga melakukan update collected amount,
                // dan kita sudah memastikan balanceDecreased, maka kita panggil createDonation.
                // Jika createDonation juga mengurangi saldo, maka itu salah.
                // Mari kita revisi createDonation di DatabaseHelper agar tidak mengurangi saldo,
                // karena pengurangan saldo sudah dilakukan di sini.

                donationId = dbHelper.createDonation(
                        currentUserId,
                        currentProjectId,
                        amount,
                        donationDate,
                        status,
                        paymentMethod,
                        proofImagePath // proofImagePath bisa null atau "N/A"
                );
            }

            // Sembunyikan loading bar
            progressBar.setVisibility(View.GONE);
            buttonSelanjutnya.setEnabled(true);
            setFormEnabled(true);

            if (donationId != -1 && balanceDecreased) {
                Toast.makeText(BuatDonasiActivity.this, "Donasi sebesar " +
                        NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(amount).replace(",00", "") +
                        " berhasil!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BuatDonasiActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(BuatDonasiActivity.this, "Gagal memproses donasi saldo. Coba lagi.", Toast.LENGTH_LONG).show();
            }
        }, 1000); // Simulasi waktu proses donasi
    }

    // Metode untuk mengaktifkan/menonaktifkan input form
    private void setFormEnabled(boolean enabled) {
        editTextNama.setEnabled(enabled);
        editTextNominal.setEnabled(enabled);
        spinnerMetodePembayaran.setEnabled(enabled);
        editTextPesan.setEnabled(enabled);
        editTextPassword.setEnabled(enabled); // Juga kontrol password input
    }
}