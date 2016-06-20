package org.davidd.connect.component.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import org.davidd.connect.component.activity.ChatActivity;
import org.davidd.connect.manager.MyNotificationManager;

public class NotificationActionService extends Service {

    public static final String DELETED_NOTIFICATION_ENTITY_BARE_JID_TAG = "DeletedNotificationEntityBareJidTag";
    public static final String USER_ENTITY_BARE_JID_TAG = "UserEntityBareJidTag";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent.hasExtra(DELETED_NOTIFICATION_ENTITY_BARE_JID_TAG)) {
            deleteNotificationLocally(intent.getStringExtra(DELETED_NOTIFICATION_ENTITY_BARE_JID_TAG));
        } else if (intent.hasExtra(ChatActivity.USER_TO_CHAT_WITH)) {
            deleteNotificationLocally(intent.getStringExtra(USER_ENTITY_BARE_JID_TAG));
            buildAndStartActivity(ChatActivity.USER_TO_CHAT_WITH, intent.getStringExtra(ChatActivity.USER_TO_CHAT_WITH));
        } else if (intent.hasExtra(ChatActivity.ROOM_NAME_TAG)) {
            deleteNotificationLocally(intent.getStringExtra(USER_ENTITY_BARE_JID_TAG));
            buildAndStartActivity(ChatActivity.ROOM_NAME_TAG, intent.getStringExtra(ChatActivity.ROOM_NAME_TAG));
        }

        return START_NOT_STICKY;
    }

    private void buildAndStartActivity(String tag, String extra) {
        Bundle bundle = new Bundle();
        bundle.putString(tag, extra);

        Intent intent = new Intent();
        intent.setClass(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(ChatActivity.class);
        taskStackBuilder.addNextIntent(intent);

        taskStackBuilder.startActivities();
    }

    private void deleteNotificationLocally(String entityBareJidAsString) {
        if (!TextUtils.isEmpty(entityBareJidAsString)) {
            MyNotificationManager.instance().deleteNotificationLocally(entityBareJidAsString);
        }
    }
}
