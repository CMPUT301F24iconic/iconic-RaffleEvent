package com.example.iconic_raffleevent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.controller.NotificationController;
import com.example.iconic_raffleevent.model.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the NotificationController class.
 * Tests for fetching notifications and marking notifications as read.
 */
public class NotificationControllerTest {

    @Mock
    private FirebaseFirestore dbMock;

    @Mock
    private CollectionReference notificationsCollectionMock;

    @Mock
    private DocumentReference documentReferenceMock;

    @Mock
    private FirebaseAttendee firebaseAttendeeMock;

    private NotificationController notificationController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        notificationController = new NotificationController();
        notificationController.db = dbMock;  // Inject mock Firestore instance
        notificationController.notificationsCollection = notificationsCollectionMock;  // Inject mock collection reference
        notificationController.firebaseAttendee = firebaseAttendeeMock;  // Inject mock FirebaseAttendee
    }

    /**
     * Test case for retrieving notifications successfully.
     * It checks if the callback onNotificationsFetched is called with the correct list of notifications.
     */
    @Test
    public void testGetNotifications_Success() {
        NotificationController.GetNotificationsCallback callback = mock(NotificationController.GetNotificationsCallback.class);
        String userId = "testUserId";
        List<Notification> mockNotifications = new ArrayList<>();
        mockNotifications.add(new Notification("1", "Test notification 1"));
        mockNotifications.add(new Notification("2", "Test notification 2"));

        doAnswer(invocation -> {
            NotificationController.GetNotificationsCallback callbackArg = invocation.getArgument(1);
            callbackArg.onNotificationsFetched(mockNotifications); // Simulate success
            return null;
        }).when(firebaseAttendeeMock).getNotifications(eq(userId), any(NotificationController.GetNotificationsCallback.class));

        notificationController.getNotifications(userId, callback);

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(callback).onNotificationsFetched(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    /**
     * Test case for a failed attempt to retrieve notifications.
     * It checks if the callback onError is triggered with the correct error message.
     */
    @Test
    public void testGetNotifications_Failure() {
        NotificationController.GetNotificationsCallback callback = mock(NotificationController.GetNotificationsCallback.class);
        String userId = "testUserId";

        doAnswer(invocation -> {
            NotificationController.GetNotificationsCallback callbackArg = invocation.getArgument(1);
            callbackArg.onError("Fetch error");  // Simulate failure
            return null;
        }).when(firebaseAttendeeMock).getNotifications(eq(userId), any(NotificationController.GetNotificationsCallback.class));

        notificationController.getNotifications(userId, callback);

        verify(callback).onError("Fetch error");
    }

    /**
     * Test case for successfully marking a notification as read.
     * It checks if the callback onSuccess is triggered upon marking as read.
     */
    @Test
    public void testMarkNotificationAsRead_Success() {
        NotificationController.MarkNotificationAsReadCallback callback = mock(NotificationController.MarkNotificationAsReadCallback.class);
        String notificationId = "testNotificationId";

        when(notificationsCollectionMock.document(notificationId)).thenReturn(documentReferenceMock);

        doAnswer(invocation -> {
            ((OnSuccessListener<Void>) invocation.getArgument(0)).onSuccess(null); // Simulate success
            return null;
        }).when(documentReferenceMock).update(eq("read"), eq(true)).addOnSuccessListener(any(OnSuccessListener.class));

        notificationController.markNotificationAsRead(notificationId, callback);

        verify(callback).onSuccess();
    }

    /**
     * Test case for a failed attempt to mark a notification as read.
     * It checks if the callback onError is triggered with the correct error message.
     */
    @Test
    public void testMarkNotificationAsRead_Failure() {
        NotificationController.MarkNotificationAsReadCallback callback = mock(NotificationController.MarkNotificationAsReadCallback.class);
        String notificationId = "testNotificationId";

        when(notificationsCollectionMock.document(notificationId)).thenReturn(documentReferenceMock);

        doAnswer(invocation -> {
            ((OnFailureListener) invocation.getArgument(1)).onFailure(new Exception("Update error"));  // Simulate failure
            return null;
        }).when(documentReferenceMock).update(eq("read"), eq(true)).addOnFailureListener(any(OnFailureListener.class));

        notificationController.markNotificationAsRead(notificationId, callback);

        verify(callback).onError("Update error");
    }
}
