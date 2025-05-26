// File: Project.java
package com.example.kumpul_dana.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Project {
    private int id;
    private String title;
    private String description;
    private String imagePath;
    private double collectedAmount;
    private double targetAmount;
    private String TimeLeft; // Menggunakan nama field TimeLeft untuk menyimpan tanggal deadline (yyyy-MM-dd)

    // Konstruktor untuk proyek baru (ID akan di-generate oleh DB)
    public Project(String title, String description, String imagePath, double collectedAmount, double targetAmount, String timeLeft) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.collectedAmount = collectedAmount;
        this.targetAmount = targetAmount;
        this.TimeLeft = timeLeft; // PERBAIKAN: Mengassign parameter 'timeLeft' ke field 'TimeLeft'
        this.id = 0;
    }

    // Konstruktor untuk proyek yang sudah ada (dengan ID)
    public Project(int id, String title, String description, String imagePath, double collectedAmount, double targetAmount, String timeLeft) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.collectedAmount = collectedAmount;
        this.targetAmount = targetAmount;
        this.TimeLeft = timeLeft; // PERBAIKAN: Mengassign parameter 'timeLeft' ke field 'TimeLeft'
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public String getTimeLeft() { // Getter untuk string deadline yang tersimpan (format: yyyy-MM-dd)
        return TimeLeft;
    }

    // Metode baru untuk menghitung dan mengembalikan sisa waktu yang diformat
    public String getFormattedTimeLeft() {
        // Tentukan format tanggal yang disimpan di database (yyyy-MM-dd)
        SimpleDateFormat storedDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedTimeLeft;

        try {
            // PERBAIKAN SINTAKS dan penggunaan field TimeLeft
            Date deadlineDate = storedDateFormat.parse(this.TimeLeft); // Memastikan kurung tutup ada
            Date currentDate = new Date(); // Dapatkan tanggal saat ini

            if (deadlineDate != null) {
                long diff = deadlineDate.getTime() - currentDate.getTime(); // Selisih dalam milidetik

                if (diff <= 0) {
                    formattedTimeLeft = "Waktu Tersisa: Berakhir";
                } else {
                    long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    if (days > 0) {
                        formattedTimeLeft = "Waktu Tersisa: " + days + " hari";
                    } else {
                        long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
                        if (hours > 0) {
                            formattedTimeLeft = "Waktu Tersisa: " + hours + " jam";
                        } else {
                            long minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
                            if (minutes > 0) {
                                formattedTimeLeft = "Waktu Tersisa: " + minutes + " menit";
                            } else {
                                formattedTimeLeft = "Waktu Tersisa: Segera Berakhir";
                            }
                        }
                    }
                }
            } else {
                formattedTimeLeft = "Waktu Tersisa: N/A (Tanggal Tidak Valid)";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Jika ada kesalahan parsing, berarti format TimeLeft di DB tidak sesuai yyyy-MM-dd
            // Ini akan terjadi jika data lama masih ada atau format tidak sesuai
            formattedTimeLeft = "Waktu Tersisa: N/A (Kesalahan Parsing)";
        }
        return formattedTimeLeft;
    }

    // --- Setters (Jika diperlukan) ---
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCollectedAmount(double collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setTimeLeft(String timeLeft) { // Setter untuk TimeLeft
        this.TimeLeft = timeLeft; // PERBAIKAN: Mengassign parameter 'timeLeft' ke field 'TimeLeft'
    }
}