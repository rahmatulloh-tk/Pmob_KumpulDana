package com.example.kumpul_dana.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kumpul_dana.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PembayaranQrisActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView backButtonQris;
    private ImageView imageViewQris;
    private Button buttonUnduhKode;
    private Button buttonSelanjutnyaQris;


    private int userId;
    private int projectId;
    private double amount;
    private String paymentMethod;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        backButtonQris = findViewById(R.id.backButtonQris);
        imageViewQris = findViewById(R.id.imageViewQris);
        buttonUnduhKode = findViewById(R.id.buttonUnduhKode);
        buttonSelanjutnyaQris = findViewById(R.id.buttonSelanjutnyaQris);

        imageViewQris.setImageResource(R.drawable.qr);
        // Ambil data donasi dari Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        amount = getIntent().getDoubleExtra("AMOUNT", 0.0);
        paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");
        message = getIntent().getStringExtra("MESSAGE");

        // Listener tombol kembali
        backButtonQris.setOnClickListener(v -> finish());

        buttonUnduhKode.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveQrisImage();
            } else {
                if (ContextCompat.checkSelfPermission(PembayaranQrisActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PembayaranQrisActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                } else {
                    saveQrisImage();
                }
            }
        });

        buttonSelanjutnyaQris.setOnClickListener(v -> {
            Intent intent = new Intent(PembayaranQrisActivity.this, UploadBuktiActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("PROJECT_ID", projectId);
            intent.putExtra("AMOUNT", amount);
            intent.putExtra("PAYMENT_METHOD", paymentMethod);
            intent.putExtra("MESSAGE", message);
            startActivity(intent);
        });
    }

    private void saveQrisImage() {
        BitmapDrawable drawable = (BitmapDrawable) imageViewQris.getDrawable();
        if (drawable == null) {
            Toast.makeText(this, "Gambar QRIS tidak tersedia untuk diunduh.", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = drawable.getBitmap();

        String filename = "QRIS_KumpulDana_" + System.currentTimeMillis() + ".jpg";
        OutputStream fos;
        Uri imageUri = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Menggunakan MediaStore untuk Android Q (API 29) ke atas
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "KumpulDanaQRIS");

                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri == null) {
                    throw new IOException("Failed to create new MediaStore record.");
                }
                fos = getContentResolver().openOutputStream(imageUri);
            } else {
                File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File appSpecificDir = new File(imagesDir, "KumpulDanaQRIS");
                if (!appSpecificDir.exists()) {
                    appSpecificDir.mkdirs();
                }
                File imageFile = new File(appSpecificDir, filename);
                fos = new FileOutputStream(imageFile);
            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close(); //

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "KumpulDanaQRIS" + File.separator + filename));
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                }

                Toast.makeText(this, "QRIS berhasil diunduh ke galeri! (Folder KumpulDanaQRIS)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Gagal membuka output stream untuk menyimpan gambar.", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal mengunduh QRIS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveQrisImage(); // Coba lagi setelah izin diberikan
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak. Tidak dapat mengunduh gambar.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}