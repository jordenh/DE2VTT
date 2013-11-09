package org.ubc.de2vtt;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

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
	
	public boolean putString(String key, String val) {
		mEditor.putString(key, val);
		return mEditor.commit();
	}
	
	public boolean putStringSet(String key, Set<String> s) {
		mEditor.putStringSet(key, s);
		return mEditor.commit();
	}
	
	/**
	 * @return value in shared prefs matching key, otherwise defaultValue
	 */
	public int getInt(String key, int defaultValue) {
		return mSharedPrefs.getInt(key, defaultValue);
	}
	
	/**
	 * @return value in shared prefs matching key, otherwise defaultVal
	 */
	public String getString(String key, String defaultVal) {
		return mSharedPrefs.getString(key, defaultVal);
	}
	
	public Set<String> getStringSet(String key) {
		return mSharedPrefs.getStringSet(key, null);
	}
}
