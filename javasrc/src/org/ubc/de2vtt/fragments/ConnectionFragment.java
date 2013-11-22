package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.R;
import org.ubc.de2vtt.SharedPreferencesManager;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Mailbox;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.notifications.notifications;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionFragment extends WINGFragment {
	public static final String SHARED_PREFS_IP = "ip";	
	public static final String SHARED_PREFS_PORT = "port";
	
	private View mParentView;
	private Activity mActivity;
	private Messenger mMessenger = Messenger.GetSharedInstance();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_connection,  container, false);
		
		SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
		setConnectToIP(man.getString(SHARED_PREFS_IP, "0.0.0.0"));
		setConnectToPort(man.getString(SHARED_PREFS_PORT, "50002"));
		
		setupOnClickListeners();
		
		mActivity = this.getActivity();
		
		updateButtonStatus();
		
		setAcceptedCommands(Command.HANDSHAKE);
		
		return mParentView;
	}

	private void setupOnClickListeners() {
		EditText et = (EditText) mParentView.findViewById(R.id.RecvdMessage);
		et.setKeyListener(null);
		et = (EditText) mParentView.findViewById(R.id.error_message_box);
		et.setKeyListener(null);
		
		Button btn = (Button) mParentView.findViewById(R.id.btnConnect);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				openSocket();
				Context context = MainActivity.getAppContext();
				notifications.notify(context, context.getResources().getString(R.string.in_msg_notification)); /// temporary!
			}
		});
		
		btn = (Button) mParentView.findViewById(R.id.btnSendMessage);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
		
		btn = (Button) mParentView.findViewById(R.id.btnCloseSocket);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeSocket();
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	public void openSocket() {		
		String ip = getConnectToIP();
		Integer port = getConnectToPort();
		
		mMessenger.openSocket(ip, port);
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
		    mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateButtonStatus();
				}
			});
		  }
		}, 1501);
	}
	
	public void sendMessage() {		
		EditText et = (EditText)mParentView.findViewById(R.id.MessageText);
		String msg = et.getText().toString();
		
		mMessenger.sendStringMessage(msg, Command.HANDSHAKE);
	}
	
	public void closeSocket() {
		mMessenger.closeSocket();
		Mailbox m = Mailbox.getSharedInstance(null);
		m.kill(getActivity());
		updateButtonStatus();
	}
	
	// Construct an IP address from the four boxes
	public String getConnectToIP() {
		String addr = "";
		EditText text_ip;
		text_ip = (EditText) mParentView.findViewById(R.id.ip1);
		addr += text_ip.getText().toString();
		text_ip = (EditText) mParentView.findViewById(R.id.ip2);
		addr += "." + text_ip.getText().toString();
		text_ip = (EditText) mParentView.findViewById(R.id.ip3);
		addr += "." + text_ip.getText().toString();
		text_ip = (EditText) mParentView.findViewById(R.id.ip4);
		addr += "." + text_ip.getText().toString();
		return addr;
	}
	
	private void setConnectToIP(String ip) {
		String[] nums = ip.split("\\.");
		
		EditText et = (EditText) mParentView.findViewById(R.id.ip1);
		et.setText(nums[0]);
		et = (EditText) mParentView.findViewById(R.id.ip2);
		et.setText(nums[1]);
		et = (EditText) mParentView.findViewById(R.id.ip3);
		et.setText(nums[2]);
		et = (EditText) mParentView.findViewById(R.id.ip4);
		et.setText(nums[3]);
	}
	
	private void setConnectToPort(String port) {
		EditText et = (EditText) mParentView.findViewById(R.id.port);
		et.setText(port);
	}
	
	public Integer getConnectToPort() {
        Integer port;
        EditText text_port;

        text_port = (EditText) mParentView.findViewById(R.id.port);
        port = Integer.parseInt(text_port.getText().toString());

        return port;
	}
	
	public void updateButtonStatus() {
		boolean canSend = Messenger.readyToSend();
		Button btn = (Button) mParentView.findViewById(R.id.btnCloseSocket);
		btn.setEnabled(canSend);
		btn = (Button) mParentView.findViewById(R.id.btnSendMessage);
		btn.setEnabled(canSend);
		btn = (Button) mParentView.findViewById(R.id.btnConnect);
		btn.setEnabled(!canSend);
	}
	
	 private void updateReceivedField(Received rcv) {
	        final String msgStr = rcv.DataToString();
	        mActivity.runOnUiThread(new Runnable() {
	            public void run() {
	            	updateButtonStatus();
	                EditText et = (EditText) mParentView.findViewById(R.id.RecvdMessage);
	                if (msgStr != null && msgStr.length() > 0) {
	                    et.setText(msgStr);
	                }
	            }
	        });
	    }

	@Override
	public boolean passReceived(Received r) {
		updateReceivedField(r);
		return true;
	}
}
