package com.gaicomo.app.utils;

/**
 * Created by admin1 on 12/10/18.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

import com.gaicomo.app.R;

/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String DOWNLOAD_CHANNEL = "Download";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationHelper(Context ctx) {
        super(ctx);

        NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL,
                getString(R.string.download), NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel);
    }


    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotification(String title) {
        return new Notification.Builder(getApplicationContext(), DOWNLOAD_CHANNEL)
                .setContentTitle(title)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(false)
                .setProgress(100,0,true)
                .setAutoCancel(true);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource tab_id
     */
    private int getSmallIcon() {
        return R.drawable.download;
    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }


}