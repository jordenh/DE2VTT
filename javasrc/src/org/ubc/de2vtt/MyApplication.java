package org.ubc.de2vtt;

import java.net.Socket;

import org.ubc.de2vtt.comm.Mailbox;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
	private static final String TAG = MyApplication.class.getSimpleName();
	Socket sock = null;
	private static Mailbox mailbox;
	
	public static byte id = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Created application.");
	}
	
	@Override
	public void onTerminate() {
		Log.d(TAG, "Application terminating.");
		super.onTerminate();
	}
	
	public static Mailbox getMailbox() {
		return mailbox;
	}
}
