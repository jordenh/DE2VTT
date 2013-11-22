package org.ubc.de2vtt.fragments;

import java.util.ArrayList;
import java.util.List;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.users.User;
import org.ubc.de2vtt.users.UserManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class PassMessageFragment extends WINGFragment {
	private static final String TAG = PassMessageFragment.class.getSimpleName();	
	
	protected View mParentView;
	private Activity mActivity;
	private Messenger mMessenger = Messenger.GetSharedInstance();
	private User mDestination;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_passmessage,  container, false);
	
		setupSpinner();
		setupOnClickListeners();

		//receiver = new SingleReceiver(new ConnectionFragmentReceiveTask());
		updateButtonState();
		
		mActivity = this.getActivity();
		
		setAcceptedCommands(Command.PASS_MSG);
		
		return mParentView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	private void setupSpinner() {
		Spinner rcvrSpinner = (Spinner) mParentView.findViewById(R.id.rcvrSpinner);
		// Obtain list of currently connected phone IDs (names), as well as title for main screen. TBD - add more 
		List<String> rcvrIDs = new ArrayList<String>();
		
		UserManager UM = UserManager.getSharedInstance();
		for(int i = 0; i < UM.count(); i++) {
			rcvrIDs.add(UM.getAtIndex(i).getAlias() + " " + UM.getAtIndex(i).getID());
		}
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
				(this.getActivity(), android.R.layout.simple_spinner_item, rcvrIDs);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		rcvrSpinner.setAdapter(adapter);
		
		rcvrSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long arg3) {
				UserManager um = UserManager.getSharedInstance();
				mDestination = um.getAtIndex(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	private void setupOnClickListeners() {		

		Button sendTokBtn = (Button) mParentView.findViewById(R.id.btnSendMessage);
		sendTokBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passMsg();
			}
		});
	}
	
	private void updateButtonState() {
		boolean canSend = Messenger.readyToSend();
		
		Button sendMsgBtn = (Button) mParentView.findViewById(R.id.btnSendMessage);
		sendMsgBtn.setEnabled(canSend);
	}
	
	
	public void passMsg() {
		EditText et = (EditText)mParentView.findViewById(R.id.sendMsg);
		Spinner sp = (Spinner)mParentView.findViewById(R.id.rcvrSpinner); // TODO: - need to make this msg string concatonation correct.
		String msg = "\0";  
		msg += et.getText().toString() + '\0';
		
		byte[] strBytes = msg.getBytes();
		strBytes[0] = (byte)mDestination.getID();
		msg = new String(strBytes);
		
		mMessenger.sendStringMessage(msg, Command.PASS_MSG);
	}

	@Override
	public boolean passReceived(Received r) {
		final String msgStr = r.DataToString();
		mActivity.runOnUiThread(new Runnable() {
            public void run() {
            	updateButtonState();
                TextView tv = (TextView) mParentView.findViewById(R.id.inMsgLabel);
                if (msgStr != null && msgStr.length() > 0) {
                    tv.setText(msgStr);
                }
            }
        });
		return false;
	}
}



