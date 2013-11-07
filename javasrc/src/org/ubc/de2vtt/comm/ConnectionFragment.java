package org.ubc.de2vtt.comm;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.SharedPreferencesManager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionFragment extends Fragment {
	private static final String TAG = ConnectionFragment.class.getSimpleName();
	private static final String SHARED_PREFS_IP = "ip";	
	
	private View mParentView;
	private Activity mActivity;
	private Messenger mMessenger = Messenger.GetSharedInstance();
	private Receiver receiver;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_connection,  container, false);
		
		SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
		setConnectToIP(man.getString(SHARED_PREFS_IP, "0.0.0.0"));
		
		setupOnClickListeners();
		
		mActivity = this.getActivity();
		
		receiver = new SingleReceiver(new TCPReadTimerTask());
		updateButtonStatus();
		
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
		if (!receiver.isTaskNull()) {
			receiver.cancel();
		}
	}
	
	public void openSocket() {
		String ip = getConnectToIP();
		Integer port = getConnectToPort();
		
		mMessenger.openSocket(ip, port);
		SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
		man.putString(SHARED_PREFS_IP, ip);
		
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
		
		mMessenger.sendStringMessage(msg);
	}
	
	public void closeSocket() {
		mMessenger.closeSocket();
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

	public class TCPReadTimerTask extends ReceiveTask {
	    protected void performAction(Received rcv) {
	    	Log.v(TAG, "Timer fires.");
	    	updateReceivedField(rcv);
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
	}
}
