/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.etek.sommerlibrary.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.etek.sommerlibrary.R;


public class NotifyUtil {

  /** Message ID Counter **/
  private static int MessageID = 0;

  /**
   * Displays a notification in the notification area of the UI
   * @param context Context from which to create the notification
   * @param messageString The string to display to the user as a message
   * @param intent The intent which will start the activity when the user clicks the notification
   * @param notificationTitle The resource reference to the notification title
   */
  public  static void notifcation(Context context, String messageString, Intent intent, int notificationTitle,String title) {


    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    final String CHANNEL_ID = "channel_id_1";
    final String CHANNEL_NAME = "channel_name_1";

    NotificationManager mNotificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      //只在Android O之上需要渠道
      NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
              CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
      //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
      //通知才能正常弹出
      mNotificationManager.createNotificationChannel(notificationChannel);
    }

    NotificationCompat.Builder builder= new NotificationCompat.Builder(context,CHANNEL_ID);


    builder.setSmallIcon(notificationTitle)
            .setContentTitle(title)
            .setContentText(messageString)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

    mNotificationManager.notify(MessageID++, builder.build());

  }



}
