package org.ubc.de2vtt.notifications;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;



public class notifications {
	private static int mId;
	private static String action;
	
	private static int numPendingMessages = 0;

    public static void notify(Context context, String action) {
    	notifications.action = action;
    	if(action == context.getResources().getString(R.string.in_msg_notification)) {
    		mId = 1;
    		numPendingMessages++;
    		notifyOfNewMessage(context);
    	} else {
    	}	
    }
    
    public static void removeNotify(Context context, String action) {
    	notifications.action = action;
    	
    	NotificationManager mNotificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
    	
    	if(action == context.getResources().getString(R.string.in_msg_notification)) {
    		mId = 1;
    		numPendingMessages = 0;
    		mNotificationManager.cancel(mId);
    	} else {
    	}
    }
		
	private static void notifyOfNewMessage(Context context) {
		
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(MainActivity.getAppContext())
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("New message in WING.")
		        .setContentText(numPendingMessages + " pending messages");

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

		if(action != null && resultIntent != null){         
			resultIntent.setAction(action);         
        }
		
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
		
		// mId allows you to update the notification later on. 
		mNotificationManager.notify(mId, mBuilder.build());
	}
	
	

}
