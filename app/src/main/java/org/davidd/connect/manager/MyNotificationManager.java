package org.davidd.connect.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import org.davidd.connect.ConnectApp;
import org.davidd.connect.R;
import org.davidd.connect.component.activity.ChatActivity;
import org.davidd.connect.component.activity.SplashActivity;
import org.davidd.connect.model.User;
import org.davidd.connect.util.BitmapUtil;

import java.util.ResourceBundle;

import static org.davidd.connect.util.DataUtils.createGsonWithExcludedFields;

public class MyNotificationManager {

    public static final int CONNECTION_SERVICE_NOTIFICATION_ID = 1;

    private static MyNotificationManager myNotificationManager;
    private NotificationManagerCompat notificationManager;

    private MyNotificationManager() {
        notificationManager =
                NotificationManagerCompat.from(ConnectApp.instance().getApplicationContext());
    }

    public static MyNotificationManager instance() {
        if (myNotificationManager == null) {
            myNotificationManager = new MyNotificationManager();
        }
        return myNotificationManager;
    }

    public void showNewMessageNotification(Context context, User sender, String message) {
        showNewMessageNotification(context, sender, message, null);
    }

    public void showNewMessageNotification(Context context, User sender, String message, Bitmap largeIcon) {
        if (largeIcon == null) {
            largeIcon = BitmapUtil.drawTextToBitmap(sender.getUserInitials());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(sender.getUserJIDProperties().getNameAndDomain())
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_small_icon)
                .setLargeIcon(largeIcon)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntentForNewMessageNotification(context, sender));

        //TODO: set ID in order to group notifications by user
        showNotification(0, builder.build());
    }

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
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    private PendingIntent createPendingIntentForNewMessageNotification(Context context, User sender) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ChatActivity.USER_TO_CHAT_WITH, createGsonWithExcludedFields().toJson(sender));

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(ChatActivity.class);
        taskStackBuilder.addNextIntent(intent);

        return taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
    }

    private void showNotification(int id, Notification notification) {
        showNotification(null, id, notification);
    }

    private void showNotification(String tag, int id, Notification notification) {
        notificationManager.notify(tag, id, notification);
    }
}
