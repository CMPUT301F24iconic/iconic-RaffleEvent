package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.QRCodeController;

import java.util.ArrayList;

/**
 * Activity for managing QR codes in the application. This activity loads a list of QR codes,
 * allows the user to view and remove QR code data through a confirmation dialog.
 */
public class QRCodeManagementActivity extends AppCompatActivity {
    private ListView qrCodeListView;
    private QRCodeController qrCodeController;
    private ArrayList<String> qrCodeDataList;
    private ArrayAdapter<String> adapter;

    /**
     * Called when the activity is created. Sets the layout, initializes the QRCodeController,
     * and loads the list of QR code data.
     *
     * @param savedInstanceState The saved state of the activity if it was previously destroyed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_management);

        qrCodeListView = findViewById(R.id.qr_code_list_view);
        qrCodeController = new QRCodeController();

        loadQRCodeDataList();
    }

    /**
     * Loads the QR code data from the QRCodeController and displays it in the ListView.
     * If QR codes are fetched successfully, they are displayed in the list. Otherwise, an error
     * message is shown.
     */
    private void loadQRCodeDataList() {
        qrCodeController.getAllQRCodeData(new QRCodeController.GetQRCodeDataCallback() {
            @Override
            public void onQRCodeDataFetched(ArrayList<String> qrCodes) {
                qrCodeDataList = qrCodes;

                adapter = new ArrayAdapter<>(QRCodeManagementActivity.this, android.R.layout.simple_list_item_1, qrCodeDataList);
                qrCodeListView.setAdapter(adapter);

                qrCodeListView.setOnItemClickListener((parent, view, position, id) -> showQRCodeOptionsDialog(qrCodeDataList.get(position)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(QRCodeManagementActivity.this, "Error loading QR codes: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays a dialog with options to remove or cancel the removal of a QR code.
     *
     * @param qrCodeData The QR code data that is being managed.
     */
    private void showQRCodeOptionsDialog(String qrCodeData) {
        new AlertDialog.Builder(this)
                .setTitle("Manage QR Code")
                .setMessage("Would you like to remove this QR code?")
                .setPositiveButton("Remove", (dialog, which) -> removeQRCode(qrCodeData))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Removes the selected QR code data by calling the QRCodeController to delete it.
     *
     * @param qrCodeData The QR code data that should be removed.
     */
    private void removeQRCode(String qrCodeData) {
        qrCodeController.deleteQRCodeData(qrCodeData, new QRCodeController.DeleteQRCodeDataCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(QRCodeManagementActivity.this, "QR code data removed successfully", Toast.LENGTH_SHORT).show();
                loadQRCodeDataList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(QRCodeManagementActivity.this, "Failed to remove QR code data: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
