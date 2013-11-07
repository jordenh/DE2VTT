package org.ubc.de2vtt.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.ubc.de2vtt.sendables.SendableString;

import android.os.AsyncTask;
import android.util.Log;

// Singleton class used to send/receive messages via middleman
// Most code copied or adapted from platform tutorial 2
public class Messenger {
	static final String TAG = Messenger.class.getSimpleName();
	
	private Socket mSocket;
	private String ip;
	private String port;
	
	static Messenger mSharedInstance;
	static ReentrantLock mutex = new ReentrantLock(true);
	
	public static Messenger GetSharedInstance() {
		if (mSharedInstance == null) {
			mSharedInstance = new Messenger();
		}
		return mSharedInstance;
	}
	
	protected Messenger() {
		mSocket = null;
		ip = null;
		port = null;
	}
	
	public synchronized void resetSocket() {
		if (ip == null || port == null) {
			Log.e(TAG, "Unable to reset null socket.");
		} 
		else if (!isConnected()) {
			Log.e(TAG, "Cannot reset non-connected socket.");
		} else {
			closeSocket();
			openSocket(ip, port);
		}
	}
	
	public synchronized void openSocket(String ip, Integer port) {
		openSocket(ip, port.toString());
	}

	public synchronized void openSocket(String ip, String port) {
		// Make sure the socket is not already opened 
		
		if (isConnected()) {
			Log.e(TAG, "Socket already open");
			return;
		}
		this.ip = ip;
		this.port = port;
		
		new SocketConnector().execute(this.ip, this.port);
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
	
	public synchronized boolean isConnected() {
		return mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
	}
	
	public void sendStringMessage(String str) {		
		SendableString sendStr = new SendableString(str);
		Message msg = new Message(Command.HANDSHAKE, sendStr);

		send(msg);
	}

	public void send(Message msg) {
		new SocketSender().execute(msg);
//		try {
//			s.get(5000, TimeUnit.MILLISECONDS);
//		} catch (InterruptedException e) {
//			closeSocket();
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			closeSocket();
//			e.printStackTrace();
//		} catch (TimeoutException e) {
//			closeSocket();
//			e.printStackTrace();
//		}
	}
	
	private class SocketSender extends AsyncTask<Message, Integer, Void> {
		@Override
		protected Void doInBackground(Message... msg) {
			mutex.lock();
			try {
				sendMessage(msg[0]);
			}
			finally {
				mutex.unlock();
			}
			return null;
		}	
		
		private void sendMessage(Message msg) {		
			byte buf[] = msg.GetArrayToSend();		
			if (isConnected()) {
				try {
					OutputStream out = mSocket.getOutputStream();
					Log.v(TAG, "Sending " + buf.length + " bytes.");
					try {
						out.write(buf, 0, buf.length);
						Log.v(TAG, "Send complete.");
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
	}
	
	public Received receive() {
		SocketReceiver task = (SocketReceiver) new SocketReceiver();
		task.execute();
		Received r = null;
		try {
			r = task.get(3000, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			Log.e(TAG, "Receive timed out.");
			resetSocket();
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.e(TAG, "Receive interrupted out.");
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.e(TAG, "Receive computation mucket up.");
			e.printStackTrace();
		}
		return r;
	}
	
	private class SocketReceiver extends AsyncTask<Void, Integer, Received> {
		@Override
		protected Received doInBackground(Void... i) {
			//mutex.lock();
			Received r = null;
			try {
				r = receiveMessage();
			}
			finally {
				//mutex.unlock();
			}
			return r;
		}
		
		public Received receiveMessage() {
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
	}
	
	// Used by SocketConnect to set the socket once the connection occurs async 
	public synchronized void setSocket(Socket sock) {
		mSocket = sock;
	}
	
	public static boolean readyToSend() {
		return GetSharedInstance().isConnected();
	}
}
