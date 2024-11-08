package com.example.iconic_raffleevent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.iconic_raffleevent.controller.ImageController;
import com.example.iconic_raffleevent.model.ImageData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the ImageController class.
 * Tests for fetching all images and deleting an image.
 */
public class ImageControllerTest {

    @Mock
    private FirebaseFirestore dbMock;

    @Mock
    private QuerySnapshot querySnapshotMock;

    @Mock
    private CollectionReference collectionReferenceMock;

    @Mock
    private DocumentReference documentReferenceMock;

    private ImageController imageController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        imageController = new ImageController();
        imageController.db = dbMock; // Inject mock Firestore instance

        when(dbMock.collection("images")).thenReturn(collectionReferenceMock);
    }

    /**
     * Test case for retrieving all images successfully.
     * It checks if the callback onImagesFetched is called with the correct list of images.
     */
    @Test
    public void testGetAllImages_Success() {
        ImageController.ImageListCallback callback = mock(ImageController.ImageListCallback.class);

        // Mock a list of QueryDocumentSnapshots to simulate returned images
        List<QueryDocumentSnapshot> documentSnapshots = new ArrayList<>();
        doReturn(documentSnapshots).when(querySnapshotMock).getDocuments();

        // Simulate a successful fetch with addOnSuccessListener
        doAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> onSuccessListener = invocation.getArgument(0);
            onSuccessListener.onSuccess(querySnapshotMock);
            return null;
        }).when(collectionReferenceMock).get().addOnSuccessListener(any(OnSuccessListener.class));

        imageController.getAllImages(callback);

        // Capture the callback to verify
        ArgumentCaptor<ArrayList<ImageData>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(callback).onImagesFetched(captor.capture());
        assertEquals(0, captor.getValue().size());
    }

    /**
     * Test case for a failed attempt to retrieve images.
     * It checks if the callback onError is triggered with the error message.
     */
    @Test
    public void testGetAllImages_Failure() {
        ImageController.ImageListCallback callback = mock(ImageController.ImageListCallback.class);

        // Simulate a failed fetch
        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(1);
            onFailureListener.onFailure(new Exception("Fetch error"));
            return null;
        }).when(collectionReferenceMock).get().addOnFailureListener(any(OnFailureListener.class));

        imageController.getAllImages(callback);

        verify(callback).onError("Error fetching images: Fetch error");
    }

    /**
     * Test case for successfully deleting an image by ID.
     * It checks if the callback onSuccess is triggered upon successful deletion.
     */
    @Test
    public void testDeleteImage_Success() {
        String imageId = "testImageId";
        ImageController.DeleteImageCallback callback = mock(ImageController.DeleteImageCallback.class);

        when(collectionReferenceMock.document(imageId)).thenReturn(documentReferenceMock);

        // Simulate successful deletion
        doAnswer(invocation -> {
            OnSuccessListener<Void> onSuccessListener = invocation.getArgument(0);
            onSuccessListener.onSuccess(null);
            return null;
        }).when(documentReferenceMock).delete().addOnSuccessListener(any(OnSuccessListener.class));

        imageController.deleteImage(imageId, callback);

        verify(callback).onSuccess();
    }

    /**
     * Test case for a failed attempt to delete an image.
     * It checks if the callback onError is triggered with the error message.
     */
    @Test
    public void testDeleteImage_Failure() {
        String imageId = "testImageId";
        ImageController.DeleteImageCallback callback = mock(ImageController.DeleteImageCallback.class);

        when(collectionReferenceMock.document(imageId)).thenReturn(documentReferenceMock);

        // Simulate failed deletion
        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(1);
            onFailureListener.onFailure(new Exception("Deletion error"));
            return null;
        }).when(documentReferenceMock).delete().addOnFailureListener(any(OnFailureListener.class));

        imageController.deleteImage(imageId, callback);

        verify(callback).onError("Failed to delete image: Deletion error");
    }
}
