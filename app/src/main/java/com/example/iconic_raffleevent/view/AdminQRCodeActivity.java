package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.QRCodeData;

import java.util.ArrayList;

/**
 * AdminQRCodeActivity provides an interface for administrators to manage QR codes within the application.
 * This activity displays a list of QR codes fetched from Firebase and allows administrators to delete QR codes.
 */
public class AdminQRCodeActivity extends AppCompatActivity {
    private ListView qrCodeListView;
    private ArrayAdapter<String> qrCodeAdapter;
    private ArrayList<QRCodeData> qrCodeList;
    private FirebaseOrganizer firebaseOrganizer;

    /**
     * Called when the activity is created. Sets up the layout and initializes the QR code list.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_qrcode);

        qrCodeListView = findViewById(R.id.qrcode_list_view);
        firebaseOrganizer = new FirebaseOrganizer();
        loadQRCodeList();
    }

    /**
     * Loads the list of QR codes from Firebase and populates the ListView with QR code names.
     * Sets up a click listener for each item in the list to allow for QR code deletion.
     */
    private void loadQRCodeList() {
        firebaseOrganizer.getAllQRCodes(new FirebaseOrganizer.GetQRCodesCallback() {
            @Override
            public void onQRCodesFetched(ArrayList<QRCodeData> qrCodes) {
                qrCodeList = qrCodes;
                ArrayList<String> qrCodeNames = new ArrayList<>();
                for (QRCodeData qrCode : qrCodes) {
                    qrCodeNames.add(qrCode.getQrCodeName());
                }
                qrCodeAdapter = new ArrayAdapter<>(AdminQRCodeActivity.this, android.R.layout.simple_list_item_1, qrCodeNames);
                qrCodeListView.setAdapter(qrCodeAdapter);
                qrCodeListView.setOnItemClickListener((adapterView, view, i, l) -> showDeleteDialog(qrCodeList.get(i)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminQRCodeActivity.this, "Error loading QR codes: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows a confirmation dialog for deleting a selected QR code.
     *
     * @param qrCode The QRCodeData object representing the QR code to be deleted.
     */
    private void showDeleteDialog(QRCodeData qrCode) {
        new AlertDialog.Builder(this)
                .setTitle("Delete QR Code")
                .setMessage("Are you sure you want to delete this QR code?")
                .setPositiveButton("Delete", (dialog, which) -> deleteQRCode(qrCode))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the specified QR code from Firebase and refreshes the QR code list on success.
     *
     * @param qrCode The QRCodeData object representing the QR code to delete.
     */
    private void deleteQRCode(QRCodeData qrCode) {
        firebaseOrganizer.deleteQRCode(qrCode.getQrCodeId(), new FirebaseOrganizer.DeleteQRCodeCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminQRCodeActivity.this, "QR code deleted successfully", Toast.LENGTH_SHORT).show();
                loadQRCodeList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminQRCodeActivity.this, "Error deleting QR code: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}