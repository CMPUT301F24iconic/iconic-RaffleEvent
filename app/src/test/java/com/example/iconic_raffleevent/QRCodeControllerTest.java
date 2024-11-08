package com.example.iconic_raffleevent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.iconic_raffleevent.controller.QRCodeController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the QRCodeController class.
 * Tests for fetching and deleting QR code data.
 */
public class QRCodeControllerTest {

    @Mock
    private FirebaseFirestore dbMock;

    @Mock
    private CollectionReference collectionReferenceMock;

    @Mock
    private DocumentReference documentReferenceMock;

    private QRCodeController qrCodeController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        qrCodeController = new QRCodeController();
        qrCodeController.db = dbMock;  // Inject mock Firestore instance
    }

    /**
     * Test case for successfully fetching all QR code data.
     * Verifies that the callback onQRCodeDataFetched is called with the correct data.
     */
    @Test
    public void testGetAllQRCodeData_Success() {
        QRCodeController.GetQRCodeDataCallback callback = mock(QRCodeController.GetQRCodeDataCallback.class);
        QuerySnapshot querySnapshotMock = mock(QuerySnapshot.class);
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        QueryDocumentSnapshot documentMock = mock(QueryDocumentSnapshot.class);

        when(dbMock.collection("qr_codes")).thenReturn(collectionReferenceMock);
        when(querySnapshotMock.getDocuments()).thenReturn(documents);

        documents.add(documentMock);
        when(documentMock.getString("data")).thenReturn("testQRCodeData");

        doAnswer(invocation -> {
            ((OnCompleteListener<QuerySnapshot>) invocation.getArgument(0)).onComplete(new Task<QuerySnapshot>() {
                @Override
                public boolean isSuccessful() {
                    return true;
                }
                @Override
                public QuerySnapshot getResult() {
                    return querySnapshotMock;
                }
                // other overridden methods...
            });
            return null;
        }).when(collectionReferenceMock).get(any(com.google.firebase.firestore.Source.class));

        qrCodeController.getAllQRCodeData(callback);

        ArgumentCaptor<ArrayList<String>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(callback).onQRCodeDataFetched(captor.capture());

        ArrayList<String> qrCodes = captor.getValue();
        assertEquals(1, qrCodes.size());
        assertEquals("testQRCodeData", qrCodes.get(0));
    }

    /**
     * Test case for a failed attempt to fetch QR code data.
     * Verifies that the callback onError is triggered with the correct error message.
     */
    @Test
    public void testGetAllQRCodeData_Failure() {
        QRCodeController.GetQRCodeDataCallback callback = mock(QRCodeController.GetQRCodeDataCallback.class);

        when(dbMock.collection("qr_codes")).thenReturn(collectionReferenceMock);

        doAnswer(invocation -> {
            ((OnCompleteListener<QuerySnapshot>) invocation.getArgument(0)).onComplete(new Task<QuerySnapshot>() {
                @Override
                public boolean isSuccessful() {
                    return false;
                }
                @Override
                public Exception getException() {
                    return new Exception("Fetch error");
                }
                // other overridden methods...
            });
            return null;
        }).when(collectionReferenceMock).get(any(com.google.firebase.firestore.Source.class));

        qrCodeController.getAllQRCodeData(callback);

        verify(callback).onError("Failed to fetch QR code data: Fetch error");
    }

    /**
     * Test case for successfully deleting QR code data.
     * Verifies that the callback onSuccess is triggered upon successful deletion.
     */
    @Test
    public void testDeleteQRCodeData_Success() {
        QRCodeController.DeleteQRCodeDataCallback callback = mock(QRCodeController.DeleteQRCodeDataCallback.class);
        Query queryMock = mock(Query.class);
        QuerySnapshot querySnapshotMock = mock(QuerySnapshot.class);
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        QueryDocumentSnapshot documentMock = mock(QueryDocumentSnapshot.class);

        when(dbMock.collection("qr_codes")).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.whereEqualTo("data", "testQRCodeData")).thenReturn(queryMock);
        when(querySnapshotMock.getDocuments()).thenReturn(documents);

        documents.add(documentMock);
        when(documentMock.getId()).thenReturn("testDocumentId");

        doAnswer(invocation -> {
            ((OnSuccessListener<QuerySnapshot>) invocation.getArgument(0)).onSuccess(querySnapshotMock);
            return null;
        }).when(queryMock).get(any(com.google.firebase.firestore.Source.class));

        doAnswer(invocation -> {
            ((OnSuccessListener<Void>) invocation.getArgument(0)).onSuccess(null);
            return null;
        }).when(documentReferenceMock).delete(any(OnSuccessListener.class));

        qrCodeController.deleteQRCodeData("testQRCodeData", callback);

        verify(callback).onSuccess();
    }

    /**
     * Test case for a failed attempt to delete QR code data.
     * Verifies that the callback onError is triggered with the correct error message.
     */
    @Test
    public void testDeleteQRCodeData_Failure() {
        QRCodeController.DeleteQRCodeDataCallback callback = mock(QRCodeController.DeleteQRCodeDataCallback.class);
        Query queryMock = mock(Query.class);

        when(dbMock.collection("qr_codes")).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.whereEqualTo("data", "nonExistentData")).thenReturn(queryMock);

        doAnswer(invocation -> {
            ((OnFailureListener) invocation.getArgument(1)).onFailure(new Exception("Delete error"));
            return null;
        }).when(queryMock).get(any(com.google.firebase.firestore.Source.class));

        qrCodeController.deleteQRCodeData("nonExistentData", callback);

        verify(callback).onError("Error deleting QR code data: Delete error");
    }
}
