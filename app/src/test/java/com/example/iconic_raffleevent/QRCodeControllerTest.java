package com.example.iconic_raffleevent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.iconic_raffleevent.controller.QRCodeController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class QRCodeControllerTest {

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private CollectionReference mockCollectionRef;

    @Mock
    private Query mockQuery;

    @Mock
    private Task<QuerySnapshot> mockTask;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private DocumentReference mockDocRef;

    @Mock
    private Task<Void> mockVoidTask;

    private QRCodeController qrCodeController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup Firebase mocks
        when(mockDb.collection(anyString())).thenReturn(mockCollectionRef);
        when(mockCollectionRef.whereEqualTo(anyString(), any())).thenReturn(mockQuery);

        // Mock Task behavior
        doAnswer(invocation -> mockTask).when(mockCollectionRef).get();
        doAnswer(invocation -> mockTask).when(mockQuery).get();

        // Mock Document Reference
        when(mockCollectionRef.document(anyString())).thenReturn(mockDocRef);
        when(mockDocRef.delete()).thenReturn(mockVoidTask);

        // Initialize controller with mocked Firebase
        qrCodeController = new QRCodeController();
        qrCodeController.db = mockDb;
    }

    @Test
    public void testGetAllQRCodeData_Success() {
        // Setup test data
        ArrayList<String> expectedQrCodes = new ArrayList<>(Arrays.asList("qr1", "qr2", "qr3"));
        QueryDocumentSnapshot mockDocument1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot mockDocument2 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot mockDocument3 = mock(QueryDocumentSnapshot.class);

        // Setup mock responses
        when(mockDocument1.getString("data")).thenReturn("qr1");
        when(mockDocument2.getString("data")).thenReturn("qr2");
        when(mockDocument3.getString("data")).thenReturn("qr3");
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.iterator()).thenReturn(Arrays.asList(
                mockDocument1, mockDocument2, mockDocument3
        ).iterator());

        // Create mock callback
        QRCodeController.GetQRCodeDataCallback callback = mock(QRCodeController.GetQRCodeDataCallback.class);

        // Setup task completion
        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any());

        // Trigger the method
        qrCodeController.getAllQRCodeData(callback);

        // Verify callback was called with correct data
        verify(callback).onQRCodeDataFetched(eq(expectedQrCodes));
    }

    @Test
    public void testGetAllQRCodeData_Error() {
        // Setup mock error
        Exception mockException = new Exception("Test error");
        when(mockTask.isSuccessful()).thenReturn(false);
        when(mockTask.getException()).thenReturn(mockException);

        // Create mock callback
        QRCodeController.GetQRCodeDataCallback callback = mock(QRCodeController.GetQRCodeDataCallback.class);

        // Setup task completion
        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any());

        // Trigger the method
        qrCodeController.getAllQRCodeData(callback);

        // Verify error callback
        verify(callback).onError(anyString());
    }

    @Test
    public void testDeleteQRCodeData_Success() {
        // Setup test data
        String testQrCode = "test_qr_code";
        QueryDocumentSnapshot mockDocument = mock(QueryDocumentSnapshot.class);

        // Setup mock responses
        when(mockQuerySnapshot.isEmpty()).thenReturn(false);
        when(mockQuerySnapshot.iterator()).thenReturn(Arrays.asList(mockDocument).iterator());
        when(mockDocument.getId()).thenReturn("doc1");

        // Mock successful deletion
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockVoidTask;
        }).when(mockVoidTask).addOnSuccessListener(any());

        // Create mock callback
        QRCodeController.DeleteQRCodeDataCallback callback = mock(QRCodeController.DeleteQRCodeDataCallback.class);

        // Setup query success
        doAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockQuerySnapshot);
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any());

        // Trigger the method
        qrCodeController.deleteQRCodeData(testQrCode, callback);

        // Verify success callback
        verify(callback).onSuccess();
    }

    @Test
    public void testDeleteQRCodeData_NotFound() {
        // Setup test data
        String testQrCode = "nonexistent_qr_code";
        when(mockQuerySnapshot.isEmpty()).thenReturn(true);

        // Create mock callback
        QRCodeController.DeleteQRCodeDataCallback callback = mock(QRCodeController.DeleteQRCodeDataCallback.class);

        // Setup query success
        doAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockQuerySnapshot);
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any());

        // Trigger the method
        qrCodeController.deleteQRCodeData(testQrCode, callback);

        // Verify error callback
        verify(callback).onError("QR code data not found");
    }

    @Test
    public void testDeleteQRCodeData_Error() {
        // Setup test data
        String testQrCode = "test_qr_code";
        Exception mockException = new Exception("Test error");

        // Create mock callback
        QRCodeController.DeleteQRCodeDataCallback callback = mock(QRCodeController.DeleteQRCodeDataCallback.class);

        // Setup query failure
        doAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(mockException);
            return mockTask;
        }).when(mockTask).addOnFailureListener(any());

        // Trigger the method
        qrCodeController.deleteQRCodeData(testQrCode, callback);

        // Verify error callback
        verify(callback).onError(anyString());
    }
}