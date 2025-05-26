package com.example.kumpul_dana.model;

// Pastikan Anda juga memiliki kelas model Donasi.java ini
// Jika belum ada, buat file ini.

public class Donasi {
    private int id;
    private String projectTitle; // Judul proyek, bukan ID proyek
    private double amount;
    private String date;
    private String status;
    private String paymentMethod;
    private String proofImagePath; // Tambah ini

    public Donasi(int id, String projectTitle, double amount, String date, String status, String paymentMethod, String proofImagePath) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.proofImagePath = proofImagePath;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getProofImagePath() {
        return proofImagePath;
    }

    // Setter methods (jika diperlukan)
    public void setId(int id) {
        this.id = id;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setProofImagePath(String proofImagePath) {
        this.proofImagePath = proofImagePath;
    }
}