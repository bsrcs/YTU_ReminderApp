package com.busra.reminder.reminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.busra.reminder.R;
import com.busra.reminder.activity.MainActivity;


public class ReminderAlarmService extends IntentService {
    private static final String TAG = ReminderAlarmService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    CharSequence name = "test ";// The user-visible name of the channel.
    int importance = NotificationManager.IMPORTANCE_HIGH;
   public static String notificationContent="Tap to view more!";

//  public void setNotificationContent(String mNotificationContent){
//      notificationContent=mNotificationContent;
//      Log.e("DESC + notification service",""+notificationContent);
//  }

    //This is a deep link intent, and needs the task stack
    public static PendingIntent getReminderPendingIntent(Context context, Uri uri) {
        Intent action = new Intent(context, ReminderAlarmService.class);
        action.setData(uri);
        action.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        //action.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public ReminderAlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {



       // set false to disable badges, Oreo exclusive

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel( CHANNEL_ID, name, importance);
            channel.setShowBadge(true);
            manager.createNotificationChannel(channel);
        }

        Uri uri = intent.getData();

        //Display a notification to view the task details
        Intent action = new Intent(this, MainActivity.class);
        action.setData(uri);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        String description = "";






        Notification note = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("You have work to do !")
                .setContentText(notificationContent)
               .setSmallIcon(R.drawable.ic_logo_24)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        Log.e("NOTIFICATION","Notification starts");
        manager.notify(NOTIFICATION_ID, note);


    }
}