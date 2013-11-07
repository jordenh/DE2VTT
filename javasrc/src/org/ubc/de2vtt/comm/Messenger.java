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
		
		if (isConnected()) {
			Log.e(TAG, "Socket already open");
			return;
		}
		
		new SocketConnector().execute(ip, port.toString());
	}
	
	public synchronized boolean isConnected() {
		return mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
	}
	
	public void sendStringMessage(String str) {		
		SendableString sendStr = new SendableString(str);
		Message msg = new Message(Command.HANDSHAKE, sendStr);

		sendMessage(msg);
	}

	public synchronized void sendMessage(Message msg) {		
		byte buf[] = msg.GetArrayToSend();		
		if (isConnected()) {
			try {
				OutputStream out = mSocket.getOutputStream();
				
				try {
					out.write(buf, 0, buf.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.v(TAG, "Attempt to send without opening socket.");
		}
	}
	
	public synchronized Received recieveMessage() {
		Received rcv = null;
		if (isConnected()) {

			try {
				rcv = getMessage(rcv);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		} else {
			Log.v(TAG, "Attempt to receive message from non-open socket.");
		}
		return rcv;
	}

	private Received getMessage(Received rcv) throws IOException {
		InputStream in = mSocket.getInputStream();

		// See if any bytes are available from the Middleman
		int bytes_avail = in.available();
		if (bytes_avail > 0) {
			// If so, read them in
			byte buf[] = new byte[bytes_avail];
			in.read(buf);

			rcv = new Received(buf);
		}
		return rcv;
	}

	public synchronized void closeSocket() {
		if (isConnected()) {
			try {
				mSocket.getOutputStream().close();
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.v(TAG, "Attempt to close non-open socket.");
		}
	}
	
	// Used by SocketConnect to set the socket once the connection occurs async 
	public synchronized void setSocket(Socket sock) {
		mSocket = sock;
	}
	
	public static boolean readyToSend() {
		return GetSharedInstance().isConnected();
	}
}
