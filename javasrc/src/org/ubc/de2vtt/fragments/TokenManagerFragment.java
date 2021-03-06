package org.ubc.de2vtt.fragments;

import java.util.List;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenImageAdapter;
import org.ubc.de2vtt.token.TokenActivity;
import org.ubc.de2vtt.token.TokenManager;
import org.ubc.de2vtt.users.DMManager;
import org.ubc.de2vtt.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class TokenManagerFragment extends WINGFragment {
	private View mParentView;
	private Activity mActivity;

    private GridView mGridView;
    private TokenImageAdapter mImageAdapter;
    
	DMManager mDMM = DMManager.getSharedInstance();
	TokenManager mTM = TokenManager.getSharedInstance();
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_movetoken,  container, false);
		mActivity = this.getActivity();
		
		mGridView = (GridView)mParentView.findViewById(R.id.gridview);
		
		List<Token> l;
		
		if (mDMM.isUserDM()) {
			// DM should be able to see all the tokens
			l = mTM.getList();
		} else {
			l = mTM.getLocalList();
		}
		mImageAdapter = new TokenImageAdapter(this.mActivity, l);
	    mGridView.setAdapter(mImageAdapter);
		
	    setupOnClickListeners();
		
		return mParentView;
	}

	private void setupOnClickListeners() {
		
		// Listener to listen for short clicks on the buttons within the grid
		// this should take the user to the tabletop view of their token
		OnItemClickListener shortListener = new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				
				((MainActivity)mActivity).switchFragment(0);
			}} ;
			
		// Listener to listen for long clicks on the buttons within the grid
		// this should take the user to the token settings activity
		OnItemLongClickListener longListener = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
				
				Intent myIntent = new Intent(mActivity.getApplicationContext(), TokenActivity.class);

				int tokID = (int)mGridView.getAdapter().getItemId(position);
				myIntent.putExtra("token_id", tokID);
				
				startActivity(myIntent);
				
				return false;
			}};
			
		mGridView.setOnItemClickListener(shortListener);
		mGridView.setOnItemLongClickListener(longListener);
	}

	@Override
	public boolean passReceived(Received r) {
		List<Token> l;
		if (mDMM.isUserDM()) {
			// DM should be able to see all the tokens
			l = mTM.getList();
		} else {
			l = mTM.getLocalList();
		}
		
		mImageAdapter.updateList(l);
		mImageAdapter.notifyDataSetChanged();
		return true;
	}
}

