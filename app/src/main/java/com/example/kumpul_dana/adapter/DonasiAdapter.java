package com.example.kumpul_dana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kumpul_dana.R;
import com.example.kumpul_dana.model.UserDonation;
import java.util.List;
import android.util.Log;
import java.text.NumberFormat;
import java.util.Locale;

public class DonasiAdapter extends RecyclerView.Adapter<DonasiAdapter.DonasiViewHolder> {

    private Context context;
    private List<UserDonation> donationList;

    public DonasiAdapter(Context context, List<UserDonation> donationList) {
        this.context = context;
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public DonasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_riwayat_donasi, parent, false);
        return new DonasiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonasiViewHolder holder, int position) {
        UserDonation donation = donationList.get(position);

        // Format jumlah donasi ke mata uang Rupiah
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedAmount = formatRupiah.format(donation.getAmount());

        holder.textViewTanggalDonasi.setText(donation.getDonationDate());
        holder.textViewStatusDonasi.setText(donation.getStatus());
        holder.textViewJudulProyek.setText(donation.getProjectTitle());
        holder.textViewDeskripsiProyek.setText(donation.getProjectDescription());
        holder.textViewDanaDidonasikan.setText(formattedAmount);
        holder.textViewPembayaranVia.setText("(pembayaran via " + donation.getPaymentMethod() + ")");
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    // Metode untuk memperbarui data di adapter
    public void updateData(List<UserDonation> newData) {
        this.donationList.clear();
        this.donationList.addAll(newData);
        notifyDataSetChanged();
    }

    public static class DonasiViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTanggalDonasi;
        TextView textViewStatusDonasi;
        TextView textViewJudulProyek;
        TextView textViewDanaDidonasikan;
        TextView textViewDeskripsiProyek;
        TextView textViewPembayaranVia;

        public DonasiViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTanggalDonasi = itemView.findViewById(R.id.textViewTanggalDonasi);
            textViewStatusDonasi = itemView.findViewById(R.id.textViewStatusDonasi);
            textViewJudulProyek = itemView.findViewById(R.id.textViewJudulProyek);
            textViewDanaDidonasikan = itemView.findViewById(R.id.textViewDanaDidonasikan);
            textViewDeskripsiProyek = itemView.findViewById(R.id.textViewDeskripsiProyek);
            textViewPembayaranVia = itemView.findViewById(R.id.textViewPembayaranVia);
        }
    }
}