package org.ubc.de2vtt;

import java.net.Socket;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
	private static final String TAG = MyApplication.class.getSimpleName();
	Socket sock = null;
	
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
}
