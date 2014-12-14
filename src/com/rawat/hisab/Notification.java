package com.rawat.hisab;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notification {
	
	 private NotificationManager mNotificationManager;
	   private int notificationID = 100;
	   Activity ac;
	   private int numMessages=0;
	  
	   
	   public Notification(Activity ac)
	   {
		   this.ac=ac;
	   }
	   
	   protected void displayNotification(String msg,int num) {
		   

		      /* Invoking the default notification service */
		      NotificationCompat.Builder  mBuilder = 
		      new NotificationCompat.Builder(ac);	

		      mBuilder.setContentTitle("Hisab");
		      mBuilder.setContentText("Your "+msg+" limit has been exceeded");
		      mBuilder.setTicker("Hisab Alert!");
		      mBuilder.setSmallIcon(R.drawable.ic_launcher);

		      /* Increase notification number every time a new notification arrives */
		    //  mBuilder.setNumber(++numMessages);
		      
		      /* Creates an explicit intent for an Activity in your app */
		      Intent resultIntent = new Intent(ac, MainActivity.class);

		      TaskStackBuilder stackBuilder = TaskStackBuilder.create(ac);
		      stackBuilder.addParentStack(MainActivity.class);

		      /* Adds the Intent that starts the Activity to the top of the stack */
		      stackBuilder.addNextIntent(resultIntent);
		      PendingIntent resultPendingIntent =
		         stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		         );

		      mBuilder.setContentIntent(resultPendingIntent);

		      mNotificationManager =
		      (NotificationManager)ac. getSystemService(Context.NOTIFICATION_SERVICE);

		      /* notificationID allows you to update the notification later on. */
		      mNotificationManager.notify(num, mBuilder.build());
		   }

		   protected void cancelNotification() {
		      mNotificationManager.cancel(notificationID);
		   }

}
