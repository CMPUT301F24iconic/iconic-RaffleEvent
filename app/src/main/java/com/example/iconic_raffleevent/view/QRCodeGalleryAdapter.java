package com.example.iconic_raffleevent.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.QRCodeData;

import java.util.List;

/**
 * Adapter for displaying QR codes in a RecyclerView.
 */
public class QRCodeGalleryAdapter extends RecyclerView.Adapter<QRCodeGalleryAdapter.QRCodeViewHolder> {

    private final List<QRCodeData> qrCodeList;
    private final Context context;
    private final QRCodeClickListener qrCodeClickListener;

    /**
     * Constructor for QRCodeGalleryAdapter.
     *
     * @param context            The application context.
     * @param qrCodeList         List of QRCodeData objects to display.
     * @param qrCodeClickListener Listener for QR code item clicks.
     */
    public QRCodeGalleryAdapter(Context context, List<QRCodeData> qrCodeList, QRCodeClickListener qrCodeClickListener) {
        this.context = context;
        this.qrCodeList = qrCodeList;
        this.qrCodeClickListener = qrCodeClickListener;
    }

    @NonNull
    @Override
    public QRCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qr_code, parent, false);
        return new QRCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QRCodeViewHolder holder, int position) {
        QRCodeData qrCodeData = qrCodeList.get(position);

        // Load the QR code image using Glide
        Glide.with(context)
                .load(qrCodeData.getQrCodeUrl())
                .placeholder(R.drawable.placeholder_image) // Placeholder image
                .error(R.drawable.error_image) // Error image
                .into(holder.qrCodeImageView);

        // Set the event name
        holder.qrCodeNameTextView.setText(qrCodeData.getQrCodeName());

        // Handle item click
        holder.itemView.setOnClickListener(v -> qrCodeClickListener.onQRCodeClick(qrCodeData));
    }

    @Override
    public int getItemCount() {
        return qrCodeList.size();
    }

    /**
     * ViewHolder for QR code items.
     */
    public static class QRCodeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView qrCodeImageView;
        private final TextView qrCodeNameTextView;

        public QRCodeViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeImageView = itemView.findViewById(R.id.qr_code_image);
            qrCodeNameTextView = itemView.findViewById(R.id.event_name);
        }
    }

    /**
     * Interface for handling QR code item clicks.
     */
    public interface QRCodeClickListener {
        void onQRCodeClick(QRCodeData qrCodeData);
    }

    /**
     * Update the QR code list dynamically.
     *
     * @param newQRCodeList New list of QR codes.
     */
    public void updateQRCodeList(List<QRCodeData> newQRCodeList) {
        qrCodeList.clear();
        qrCodeList.addAll(newQRCodeList);
        notifyDataSetChanged();
    }
}
