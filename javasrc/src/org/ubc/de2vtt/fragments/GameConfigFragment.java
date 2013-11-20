package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.MyApplication;
import org.ubc.de2vtt.R;
import org.ubc.de2vtt.SharedPreferencesManager;
import org.ubc.de2vtt.comm.ReceiveTask;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.token.ImageAdapter;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GameConfigFragment extends Fragment {
	private static final String TAG = GameConfigFragment.class.getSimpleName();
	public static final String SHARED_PREFS_DM = "isDM";
	
	private View mParentView;
	private Activity mActivity;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_gameconfig,  container, false);
		mActivity = this.getActivity();
		
		Button btnGetReleaseDM = (Button) mParentView.findViewById(R.id.btnGetReleaseDM);
		
		SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
		if (man.getBoolean(GameConfigFragment.SHARED_PREFS_DM, false)) {
			btnGetReleaseDM.setText(R.string.button_releaseDM);
		} else {
			btnGetReleaseDM.setText(R.string.button_getDM);
		}
		
	    setupOnClickListeners();
	    
		return mParentView;
	}
	
	private void setupOnClickListeners() {
		Button btnGetReleaseDM = (Button) mParentView.findViewById(R.id.btnGetReleaseDM);
		
	}
}
