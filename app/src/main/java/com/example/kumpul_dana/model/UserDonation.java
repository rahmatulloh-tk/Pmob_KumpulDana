package com.example.kumpul_dana.model;

public class UserDonation {
    private int id;
    private int userId;
    private int projectId;
    private String projectTitle;        // Tambahkan ini
    private String projectDescription;  // Tambahkan ini
    private double amount;
    private String donationDate;
    private String status;              // Tambahkan ini
    private String paymentMethod;
    private String proofImagePath;      // Tambahkan ini

    // Konstruktor untuk membuat donasi baru (misalnya dari UI) - Ini tidak akan digunakan langsung untuk insert ke DB,
    // karena insert ke DB dilakukan di UploadBuktiActivity
    public UserDonation(int userId, int projectId, double amount, String donationDate, String status, String paymentMethod, String proofImagePath) {
        this.userId = userId;
        this.projectId = projectId;
        this.amount = amount;
        this.donationDate = donationDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.proofImagePath = proofImagePath;
    }

    // Konstruktor untuk mengambil donasi dari database (DENGAN DETAIL PROYEK)
    // Ini adalah konstruktor yang akan digunakan oleh DatabaseHelper.getDonationsByUserIdWithProjectTitle
    public UserDonation(int id, int userId, int projectId, String projectTitle, String projectDescription,
                        double amount, String donationDate, String status, String paymentMethod, String proofImagePath) {
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.amount = amount;
        this.donationDate = donationDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.proofImagePath = proofImagePath;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public double getAmount() {
        return amount;
    }

    public String getDonationDate() {
        return donationDate;
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

    // --- Setters (Jika diperlukan) ---
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
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