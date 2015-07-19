package com.rawat.hisab;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Notification {
	
	 private NotificationManager mNotificationManager;
	   private int notificationID = 100;
	   Activity ac;
	   private Context ct;
	  
	   
	   public Notification(Context c)
	   {
		   this.ct=c;
	   }
	   
	   protected void displayNotification(String msg,int num) {
		   
		   SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ct.getApplicationContext());
		   
		   if(SP.getBoolean("notifications_new_message", false))
			{

		      /* Invoking the default notification service */
		      NotificationCompat.Builder  mBuilder = 
		      new NotificationCompat.Builder(ct);	

		      mBuilder.setContentTitle("Hisab");
		      mBuilder.setContentText("Your "+msg+" limit has been exceeded");
		      mBuilder.setTicker("Hisab Alert!");
		      mBuilder.setSmallIcon(R.drawable.ic_launcher);
		      ringtone();
		      /* Increase notification number every time a new notification arrives */
		    //  mBuilder.setNumber(++numMessages);
		      
		      /* Creates an explicit intent for an Activity in your app */
		      Intent resultIntent = new Intent(ct, MainActivity.class);

		      TaskStackBuilder stackBuilder = TaskStackBuilder.create(ct);
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
		      (NotificationManager)ct. getSystemService(Context.NOTIFICATION_SERVICE);

		      mBuilder.setAutoCancel(true);
		      /* notificationID allows you to update the notification later on. */
		      mNotificationManager.notify(num, mBuilder.build());
		      //mNotificationManager.cancel(num);
			}
		   }

		   protected void cancelNotification() {
		      mNotificationManager.cancel(notificationID);
		   }
		   public void ringtone() {
			    try {
			        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			        Ringtone r = RingtoneManager.getRingtone(ct.getApplicationContext(), notification);
			        r.play();
			    } catch (Exception e) {
			    	Log.w("In Ringtone", "Error in ringtone");
			    }
			}

}
