package org.ubc.de2vtt.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TimerTask;

import android.util.Log;

public class Messenger {
	static final String TAG = Messenger.class.getSimpleName();

	private Socket mSocket;
	
	static Messenger mSharedInstance;
	
	public static Messenger GetSharedInstance() {
		if (mSharedInstance == null) {
			mSharedInstance = new Messenger();
		}
		return mSharedInstance;
	}
	
	protected Messenger() {
		mSocket = null;
	}

	// Route called when the user presses "connect"
	public void openSocket(String ip, Integer port) {
		//TextView msgbox = (TextView) findViewById(R.id.error_message_box);

		// Make sure the socket is not already opened 
		
		if (mSocket != null && mSocket.isConnected() && !mSocket.isClosed()) {
			//msgbox.setText("Socket already open");
			Log.e(TAG, "Socket already open");
			return;
		}
		
		// open the socket.  SocketConnect is a new subclass
	    // (defined below).  This creates an instance of the subclass
		// and executes the code in it.
		
		new SocketConnect().execute(ip, port.toString());
	}
	
	public void sendMessage(String msg) {		
		// Get the message from the box

		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
		byte buf[] = new byte[msg.length() + 1];
		buf[0] = (byte) msg.length(); 
		System.arraycopy(msg.getBytes(), 0, buf, 1, msg.length());

		// Now send through the output stream of the socket
		OutputStream out;
		try {
			out = mSocket.getOutputStream();
			try {
				out.write(buf, 0, msg.length() + 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Called when the user closes a socket
	public void closeSocket() {
		try {
			mSocket.getOutputStream().close();
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			if (mSocket != null && mSocket.isConnected() && !mSocket.isClosed()) {

				try {
					InputStream in = mSocket.getInputStream();

					// See if any bytes are available from the Middleman
					int bytes_avail = in.available();
					if (bytes_avail > 0) {

						// If so, read them in and create a sring
						byte buf[] = new byte[bytes_avail];
						in.read(buf);

						//final String s = new String(buf, 0, bytes_avail, "US-ASCII");

						// As explained in the tutorials, the GUI can not be
						// updated in an asyncrhonous task. So, update the GUI
						// using the UI thread.
//						runOnUiThread(new Runnable() {
//							public void run() {
//								// Do Something
//							}
//						});

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
