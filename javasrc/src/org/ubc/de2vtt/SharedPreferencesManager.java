package org.ubc.de2vtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesManager {
	//private final String TAG = SharedPreferencesManager.class.getSimpleName();
	
	private static final String SHARED_PREFS_NAME = "DE2VTTPrefs";
	
	private static SharedPreferencesManager mSharedInstance;
	
	private SharedPreferences mSharedPrefs;
	private Editor mEditor;
	
	/**
	 * Gets the shared preferences manager singleton
	 * @return Shared instance of the managers
	 */
	public static SharedPreferencesManager getSharedInstance() {
		if (mSharedInstance == null) {
			mSharedInstance = new SharedPreferencesManager();
		}
		return mSharedInstance;
	}
	
	protected SharedPreferencesManager() {
		Context context = MainActivity.getAppContext();
		mSharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, 
				Context.MODE_PRIVATE);
		mEditor = mSharedPrefs.edit();
	}
	
	// TODO: Add putXXX / getXXX methods as needed

	public boolean putInt(String key, int val) {
		mEditor.putInt(key, val);
		return mEditor.commit();
	}
	
	/**
	 * @return value in shared prefs matching key, otherwise defaultValue
	 */
	public int getInt(String key, int defaultValue) {
		return mSharedPrefs.getInt(key, defaultValue);
	}
}
