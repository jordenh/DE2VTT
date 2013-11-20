package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.tabletop.TokenAdapter;
import org.ubc.de2vtt.tabletop.TableTopOnTouchListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class TableTopFragment extends Fragment {
	protected View mParentView;
	private Activity mActivity;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_tabletop,  container, false);
		mActivity = this.getActivity();;
	    
	    setupOnClickListeners();
	  		
		return mParentView;
	}

	private void setupOnClickListeners() {
		
	}
}
