package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.token.ImageAdapter;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameConfigFragment extends Fragment {
	private View mParentView;
	private Activity mActivity;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_movetoken,  container, false);
		mActivity = this.getActivity();
		
	    setupOnClickListeners();
		
		return mParentView;
	}
	
	private void setupOnClickListeners() {
		
		
	}
}
