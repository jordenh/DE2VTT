package org.ubc.de2vtt.notifications;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.R;
import org.ubc.de2vtt.fragments.BulletinFragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;



public class notifications {
		
	public static void notifyOfNewMessage() {
		Context context = MainActivity.getAppContext();
		int mId = 1;
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(MainActivity.getAppContext())
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("New message in WING.")
		        .setContentText("Pending message from __XX__!");

		
		
		// Creates an explicit intent for an Activity in your app
		
		//BulletinFragment.class.getPackage().toString()
		//Intent resultIntent = context.getPackageManager().getLaunchIntentForPackage("org.ubc.de2vtt.fra");//new Intent(context, BulletinFragment.class);
		Intent resultIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());//new Intent(context, BulletinFragment.class);

		String action = "NOTIFY_NEW_MESSAGE";
		if(action != null && resultIntent != null){         
			resultIntent.setAction(action);         
        }
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.getAppContext());
		// Adds the back stack for the Intent (but not the Intent itself)
//		stackBuilder.addParentStack(BulletinFragment.class);
		// Adds the Intent that starts the Activity to the top of the stack
/*		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        ); */
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
		// mId allows you to update the notification later on. 
		mNotificationManager.notify(mId, mBuilder.build());
	}

}

/* old code dump:
 * 
 * 		
		//Temp to try to issue a basic notification:
		NotificationManager mNotificationManager =
		    (NotificationManager) getNotificationManager();
		NotificationManager.notify(0, mBuilder);
		// mId allows you to update the notification later on.
		//mNotificationManager.notify(mId, mBuilder.build());



*/
