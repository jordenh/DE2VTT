package org.ubc.de2vtt.comm;

import java.util.Timer;
import java.util.TimerTask;

import org.ubc.de2vtt.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionFragment extends Fragment {
	
	private View mParentView;
	private Activity mActivity;
	private TCPReadTimerTask mTimerTask;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_connection,  container, false);
		
		// TODO: save/load ip
		
		setupOnClickListeners();
		
		mActivity = this.getActivity();
		
		TCPReadTimerTask mTimerTask = new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(mTimerTask, 3000, 500);
		
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
	
	public void onDestroyView() {
		mTimerTask.cancel();
	}
	
	public void openSocket() {
		Messenger msg = Messenger.GetSharedInstance();
		String ip = getConnectToIP();
		Integer port = getConnectToPort();
		msg.openSocket(ip, port);
	}
	
	public void sendMessage() {
		Messenger messenger = Messenger.GetSharedInstance();
		
		EditText et = (EditText)mParentView.findViewById(R.id.MessageText);
		String msg = et.getText().toString();
		
		messenger.sendMessage(msg);
	}
	
	public void closeSocket() {
		Messenger messenger = Messenger.GetSharedInstance();
		messenger.closeSocket();
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
	
	public Integer getConnectToPort() {
		Integer port;
		EditText text_port;

		text_port = (EditText) mParentView.findViewById(R.id.port);
		port = Integer.parseInt(text_port.getText().toString());

		return port;
	}
	
	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			Messenger messenger = Messenger.GetSharedInstance();
			final String msg = messenger.recieveMessage();
			mActivity.runOnUiThread(new Runnable() {
				public void run() {
					EditText et = (EditText) mParentView.findViewById(R.id.RecvdMessage);
					if (msg.length() > 0) {
						et.setText(msg);
					}
				}
			});
		}
	}
}
