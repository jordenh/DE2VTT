package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.sendables.SendableNull;
import org.ubc.de2vtt.users.DMManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameConfigFragment extends WINGFragment {	
	private static final String TAG = GameConfigFragment.class.getSimpleName();
	
	protected View mParentView;
	private Activity mActivity;
	private Messenger mMessenger = Messenger.GetSharedInstance();
	private DMManager mDMMan = DMManager.getSharedInstance();
	
	private Button mUpdateAliasBtn;
    private Button mGetDMBtn;
    private Button mReleaseDMBtn;
	private TextView mDMName;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_gameconfig,  container, false);
	
		Messenger messenger = Messenger.GetSharedInstance();
		Message msg = new Message(Command.GET_DM_ID, SendableNull.GetSharedInstance());
		messenger.send(msg);
		
		mUpdateAliasBtn = (Button)mParentView.findViewById(R.id.btnUpdateAlias);
        mGetDMBtn = (Button)mParentView.findViewById(R.id.btnGetDM);
        mReleaseDMBtn  = (Button) mParentView.findViewById(R.id.btnReleaseDM);
		mDMName = (TextView)mParentView.findViewById(R.id.dmName);
		
		setupOnClickListeners();

		updateButtonAndFieldState();
		
		mActivity = this.getActivity();
		
		return mParentView;
	}

	private void setupOnClickListeners() {		
		
		mUpdateAliasBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passMsg();
			}
		});
		
		mGetDMBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message(Command.GET_DM, SendableNull.GetSharedInstance());
		        
		        mMessenger.send(msg);
			}
		});
		
		mReleaseDMBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message(Command.RELEASE_DM, SendableNull.GetSharedInstance());
		        
				mMessenger.send(msg);
			}
		});
	}
	
	private void updateButtonAndFieldState() {
		boolean canSend = Messenger.readyToSend();
		
		mUpdateAliasBtn.setEnabled(canSend);
		mGetDMBtn.setEnabled(canSend);
		mReleaseDMBtn.setEnabled(canSend);
		
		if (mDMMan.isDMAvailable()) {
			mGetDMBtn.setVisibility(View.VISIBLE);
			mDMName.setText("");
		} else {
			mGetDMBtn.setVisibility(View.VISIBLE);
			mGetDMBtn.setEnabled(false);
			mDMName.setText(mDMMan.getDMAlias());
		}
		
		if (mDMMan.isUserDM()) {
			mGetDMBtn.setVisibility(View.GONE);
			mReleaseDMBtn.setVisibility(View.VISIBLE);
			mDMName.setText("You are the DM");
		} else {
			mGetDMBtn.setVisibility(View.VISIBLE);
			mReleaseDMBtn.setVisibility(View.GONE);
		}
	}
	
	public void passMsg() {
		EditText et = (EditText)mParentView.findViewById(R.id.aliasString);
		String msg = et.getText().toString() + '\0';
		
		if(msg.length() > 1 && msg.length() < 41){
			mMessenger.sendStringMessage(msg, Command.UPDATE_ALIAS);
		} else {
			Log.v(TAG, "update of alias didn't get sent, as the string must be between 1-40 characters long.");
		}
	}
	
	@Override
	public boolean passReceived(Received r) {
		mActivity.runOnUiThread(new Runnable() {
            public void run() {
            	updateButtonAndFieldState();
            }
        });
		return false;
	}	
}
