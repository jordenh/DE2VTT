package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.tabletop.TokenAdapter;
import org.ubc.de2vtt.tabletop.TableTopOnTouchListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class TableTopFragment extends WINGFragment {
	protected View mParentView;
	private Activity mActivity;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_tabletop,  container, false);
		mActivity = this.getActivity();;
	    
	    setupOnClickListeners();
	    
	    //setAcceptedCommands(new Command[0]);
	  		
		return mParentView;
	}

	private void setupOnClickListeners() {
		
	}

	@Override
	public boolean passReceived(Received r) {
		// TODO Move token
		return false;
	}
}
