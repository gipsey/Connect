package org.davidd.connect.manager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.R;
import org.davidd.connect.component.activity.ChatActivity;
import org.davidd.connect.component.activity.SplashActivity;
import org.davidd.connect.component.service.NotificationActionService;
import org.davidd.connect.model.MyMessage;
import org.davidd.connect.model.User;
import org.davidd.connect.model.UserJIDProperties;
import org.davidd.connect.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class MyNotificationManager {

    public static final int CONNECTION_SERVICE_NOTIFICATION_ID = 1;

    private static MyNotificationManager myNotificationManager;
    private NotificationManagerCompat notificationManagerCompat;

    private static List<NotificationWrapper> activeNotifications = new ArrayList<>();
    private static int uniqueInt;

    private MyNotificationManager() {
        notificationManagerCompat =
                NotificationManagerCompat.from(ConnectApp.instance().getApplicationContext());
    }

    public static MyNotificationManager instance() {
        if (myNotificationManager == null) {
            myNotificationManager = new MyNotificationManager();
        }
        return myNotificationManager;
    }

    public void newMessageProcessed(MyMessage myMessage) {
        if (!shouldShowNotification(myMessage)) {
            return;
        }

        int id = deleteNotificationLocally(myMessage.getEntityToChatWith().toString());
        if (id == -1) {
            id = ++uniqueInt;
        }

        String messageBody;
        if (UserManager.instance().getCurrentUser().equals(myMessage.getSender())) {
            messageBody = "You wrote: ";
        } else {
            messageBody = myMessage.getSender().getUserJIDProperties().getName() + " wrote: ";
        }
        messageBody += myMessage.getMessageBody();

        Notification notification = buildNotification(
                myMessage.getEntityToChatWith().toString().substring(0, 1),
                myMessage.getEntityToChatWith().toString(),
                messageBody,
                createIntentForNewMessage(myMessage),
                createDeleteIntent(myMessage),
                null);

        NotificationWrapper notificationWrapper = new NotificationWrapper(id, notification, myMessage);

        activeNotifications.add(notificationWrapper);

        showNotification(notificationWrapper);
    }

    public int deleteNotificationLocally(String entityBareJid) {
        int id = -1;

        for (int i = 0; i < activeNotifications.size(); i++) {
            if (entityBareJid.equals(activeNotifications.get(i).getMyMessage().getEntityToChatWith().toString())) {
                return activeNotifications.remove(i).getId();
            }
        }

        return id;
    }

    private boolean shouldShowNotification(MyMessage myMessage) {
        if (myMessage.getSender().equals(UserManager.instance().getCurrentUser())) {
            for (NotificationWrapper wrapper : activeNotifications) {
                if (wrapper.getMyMessage().getEntityToChatWith().equals(myMessage.getEntityToChatWith())) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    @SuppressLint("InlinedApi")
    private Notification buildNotification(String initials, String title, String contentText,
                                           PendingIntent clickIntent, PendingIntent deleteIntent, @Nullable Bitmap largeIcon) {
        if (largeIcon == null) {
            largeIcon = BitmapUtil.drawTextToBitmap(initials);
        }

        Context context = ConnectApp.instance().getApplicationContext();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.notification_small_icon)
                .setLargeIcon(largeIcon)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentIntent(clickIntent)
                .setDeleteIntent(deleteIntent);

        return builder.build();
    }

    @SuppressLint("InlinedApi")
    public Notification createServiceMainNotification(Context context) {
        String contentText;
        Class activityClass;

        User user = UserManager.instance().getCurrentUser();

        if (user == null) {
            contentText = "Welcome guest! Please register or log in!";
            activityClass = SplashActivity.class;
        } else {
            contentText = "Welcome " + user.getUserJIDProperties().getName() + "! Let's start chatting!";
            activityClass = ResourceBundle.Control.class;
        }

        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(activityClass);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Connect")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.notification_small_icon)
                .setOngoing(false)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    private PendingIntent createIntentForNewMessage(MyMessage myMessage) {
        Context context = ConnectApp.instance().getApplicationContext();

        Intent intent = new Intent(context, NotificationActionService.class);

        if (myMessage.getType() == MyMessage.Type.NORMAL) {
            String userAsJsonFormattedString =
                    createGsonWithExcludedFields().toJson(new User(new UserJIDProperties(myMessage.getEntityToChatWith().toString())));
            intent.putExtra(ChatActivity.USER_TO_CHAT_WITH, userAsJsonFormattedString);
        } else {
            intent.putExtra(ChatActivity.ROOM_NAME_TAG, myMessage.getEntityToChatWith().toString());
        }
        intent.putExtra(NotificationActionService.USER_ENTITY_BARE_JID_TAG, myMessage.getEntityToChatWith().toString());

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private PendingIntent createDeleteIntent(MyMessage myMessage) {
        Context context = ConnectApp.instance().getApplicationContext();

        Intent intent = new Intent(context, NotificationActionService.class);
        intent.putExtra(NotificationActionService.DELETED_NOTIFICATION_ENTITY_BARE_JID_TAG, myMessage.getEntityToChatWith().toString());

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void showNotification(NotificationWrapper wrapper) {
        notificationManagerCompat.notify(wrapper.getId(), wrapper.getNotification());
    }

    private class NotificationWrapper {

        private int id;
        private Notification notification;
        private MyMessage myMessage;

        public NotificationWrapper(int id, Notification notification, MyMessage myMessage) {
            this.id = id;
            this.notification = notification;
            this.myMessage = myMessage;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Notification getNotification() {
            return notification;
        }

        public void setNotification(Notification notification) {
            this.notification = notification;
        }

        public MyMessage getMyMessage() {
            return myMessage;
        }

        public void setMyMessage(MyMessage myMessage) {
            this.myMessage = myMessage;
        }
    }
}
