package org.ubc.de2vtt.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TimerTask;

import org.ubc.de2vtt.sendables.SendableString;

import android.util.Log;

// Singleton class used to send/receive messages via middleman
// Most code copied or adapted from platform tutorial 2
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

	public synchronized void openSocket(String ip, Integer port) {
		// Make sure the socket is not already opened 
		
		if (mSocket != null && mSocket.isConnected() && !mSocket.isClosed()) {
			//msgbox.setText("Socket already open");
			Log.e(TAG, "Socket already open");
			return;
		}
		
		// open the socket.  SocketConnect is a new subclass
	    // (defined below).  This creates an instance of the subclass
		// and executes the code in it.
		
		new SocketConnector().execute(ip, port.toString());
	}
	
	public synchronized boolean isConnected() {
		return mSocket.isConnected() && !mSocket.isClosed();
	}
	
	public void sendStringMessage(String str) {		
		SendableString sendStr = new SendableString(str);
		Message msg = new Message(Command.HANDSHAKE, sendStr);

		sendMessage(msg);
	}
	
	public synchronized void sendMessage(Message msg) {		
		if (mSocket == null || mSocket.isClosed() || 
				!mSocket.isConnected()) return;
		byte buf[] = msg.GetArrayToSend();		
		
		// Now send through the output stream of the socket
		OutputStream out;
		try {
			out = mSocket.getOutputStream();
			
			try {
				out.write(buf, 0, buf.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 256 bytes in middleman buffer
	public synchronized Received recieveMessage() {
		Received rcv = null;
		if (mSocket != null && mSocket.isConnected() 
				&& !mSocket.isClosed()) {

			try {
				InputStream in = mSocket.getInputStream();

				// See if any bytes are available from the Middleman
				int bytes_avail = in.available();
				if (bytes_avail > 0) {
					
					// If so, read them in
					byte buf[] = new byte[bytes_avail];
					in.read(buf);

					rcv = Message.GetReceived(buf);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return rcv;
	}

	public synchronized void closeSocket() {
		try {
			mSocket.getOutputStream().close();
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Used by SocketConnect to set the socket once the connection occurs async 
	public void setSocket(Socket sock) {
		mSocket = sock;
	}
	
	// Not particularly useful here.
	// Could be useful going forward to manage message timers in another file
	// Otherwise we'll need these timers everywhere we want to receive info
	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			if (mSocket != null && mSocket.isConnected() && !mSocket.isClosed()) {

				try {
					InputStream in = mSocket.getInputStream();

					// See if any bytes are available from the Middleman
					int bytes_avail = in.available();
					if (bytes_avail > 0) {

						// If so, read them in and create a string
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
