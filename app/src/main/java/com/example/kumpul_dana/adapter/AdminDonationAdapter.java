package com.example.kumpul_dana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.model.UserDonation; // Menggunakan model UserDonation

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminDonationAdapter extends RecyclerView.Adapter<AdminDonationAdapter.AdminDonationViewHolder> {

    private Context context;
    private List<UserDonation> donationList;

    public AdminDonationAdapter(Context context, List<UserDonation> donationList) {
        this.context = context;
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public AdminDonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_donation, parent, false);
        return new AdminDonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDonationViewHolder holder, int position) {
        UserDonation donation = donationList.get(position);

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedAmount = formatRupiah.format(donation.getAmount()).replace(",00", "");

        holder.textViewDonationId.setText("ID: #" + donation.getId());
        holder.textViewDonationDate.setText("Tanggal: " + donation.getDonationDate());
        // Asumsi model UserDonation memiliki getter untuk username
        // Jika tidak, Anda perlu memodifikasi model UserDonation atau query di DatabaseHelper
        holder.textViewDonorName.setText("Donor: " + donation.getUserName()); // Anda harus menambahkan field userName di model UserDonation
        holder.textViewProjectTitle.setText("Proyek: " + donation.getProjectTitle());
        holder.textViewDonationAmount.setText("Nominal: " + formattedAmount);
        holder.textViewPaymentMethod.setText("Via: " + donation.getPaymentMethod());
        holder.textViewDonationStatus.setText("Status: " + donation.getStatus());

        // Atur warna status
        if (donation.getStatus().equalsIgnoreCase("Berhasil")) {
            holder.textViewDonationStatus.setTextColor(context.getResources().getColor(R.color.teal_700));
        } else if (donation.getStatus().equalsIgnoreCase("Pending")) {
            holder.textViewDonationStatus.setTextColor(context.getResources().getColor(R.color.orange_700));
        } else { // Failed, Canceled, etc.
            holder.textViewDonationStatus.setTextColor(context.getResources().getColor(R.color.red_500));
        }
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class AdminDonationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDonationId;
        TextView textViewDonationDate;
        TextView textViewDonorName;
        TextView textViewProjectTitle;
        TextView textViewDonationAmount;
        TextView textViewPaymentMethod;
        TextView textViewDonationStatus;

        public AdminDonationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDonationId = itemView.findViewById(R.id.textViewDonationId);
            textViewDonationDate = itemView.findViewById(R.id.textViewDonationDate);
            textViewDonorName = itemView.findViewById(R.id.textViewDonorName);
            textViewProjectTitle = itemView.findViewById(R.id.textViewProjectTitle);
            textViewDonationAmount = itemView.findViewById(R.id.textViewDonationAmount);
            textViewPaymentMethod = itemView.findViewById(R.id.textViewPaymentMethod);
            textViewDonationStatus = itemView.findViewById(R.id.textViewDonationStatus);
        }
    }
}