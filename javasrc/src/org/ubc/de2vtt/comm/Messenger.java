package org.ubc.de2vtt.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.ubc.de2vtt.Disconnect;
import org.ubc.de2vtt.SharedPreferencesManager;
import org.ubc.de2vtt.comm.sendables.SendableString;
import org.ubc.de2vtt.fragments.ConnectionFragment;

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
			connectWithPrevValues();
		}
		return mSharedInstance;
	}
	
	protected Messenger() {
		mSocket = null;
		ip = null;
		port = null;
	}

	private static void connectWithPrevValues() {
		SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
		mSharedInstance.ip = man.getString(ConnectionFragment.SHARED_PREFS_IP, null);
		mSharedInstance.port = man.getString(ConnectionFragment.SHARED_PREFS_PORT, null);
		if (mSharedInstance.ip != null && mSharedInstance.port != null) {
			mSharedInstance.openSocket(mSharedInstance.ip, mSharedInstance.port);
		}
	}
	
	public synchronized void resetSocket() {
//		if (ip == null || port == null) {
//			Log.e(TAG, "Unable to reset null socket.");
//		} 
//		else {
//			if (isConnected()) {
//				closeSocket();
//			}
//			openSocket(ip, port);
//		}
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
				if(!isConnected()) {
					Disconnect.removeSessionData(); 
					Log.v(TAG, "Disconnect code hit");
				}
				e.printStackTrace();
			}
		} else {
			Log.v(TAG, "Attempt to close non-open socket.");
		}
	}
	
	public synchronized boolean isConnected() {
		return mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
	}
	
	public void sendStringMessage(String str, Command cmd) {		
		SendableString sendStr = new SendableString(str);
		Message msg = new Message(cmd, sendStr);

		send(msg);
	}

	public void send(Message msg) {
		new SocketSender().execute(msg);
	}
	
	private class SocketSender extends AsyncTask<Message, Integer, Void> {
		@Override
		protected Void doInBackground(Message... msg) {
			mutex.lock();
			try {
				Thread.sleep(msg[0].getDelay());
				sendMessage(msg[0]);
			} catch (InterruptedException e) {
				if(!isConnected()) {
					Disconnect.removeSessionData(); 
					Log.v(TAG, "Disconnect code hit");
				}
				e.printStackTrace();
			}
			finally {
				mutex.unlock();
			}
			return null;
		}	
		
		private void sendMessage(Message msg) {		
			byte buf[] = msg.GetArrayToSend();	
			if (!isConnected()) {
				resetSocket();
			}
			
			if (isConnected()) {
				try {
					OutputStream out = mSocket.getOutputStream();
					Log.v(TAG, "Sending " + buf.length + " bytes.");
					try {
						out.write(buf, 0, buf.length);
						out.flush();
						Log.v(TAG, "Send complete.");
					} catch (IOException e) {
						if(!isConnected()) {
							Disconnect.removeSessionData(); 
							Log.v(TAG, "Disconnect code hit");
						}
						e.printStackTrace();
					}
				} catch (IOException e) {
					if(!isConnected()) {
						Disconnect.removeSessionData(); 
						Log.v(TAG, "Disconnect code hit");
					}
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
			if(!isConnected()) {
				Disconnect.removeSessionData(); 
				Log.v(TAG, "Disconnect code hit");
			}
			Log.e(TAG, "Receive timed out.");
			resetSocket();
			//r = attemptReceiveRecovery(r);
			//e.printStackTrace();
		} catch (InterruptedException e) {
			if(!isConnected()) {
				Disconnect.removeSessionData(); 
				Log.v(TAG, "Disconnect code hit");
			}
			resetSocket();
			Log.e(TAG, "Receive interrupted out.");
			//e.printStackTrace();
		} catch (ExecutionException e) {
			if(!isConnected()) {
				Disconnect.removeSessionData(); 
				Log.v(TAG, "Disconnect code hit");
			}
			resetSocket();
			Log.e(TAG, "Receive computation mucked up.");
			//e.printStackTrace();
		}
		return r;
	}
	
	private class SocketReceiver extends AsyncTask<Void, Integer, Received> {
		@Override
		protected Received doInBackground(Void... i) {
			mutex.lock();
			Received r = null;
			try {
				//Log.v(TAG, "Trying receive");
				r = receiveMessage();
			}
			finally {
				mutex.unlock();
			}
			return r;
		}
		
		public Received receiveMessage() {
			Received rcv = null;
			if (isConnected()) {
				try {
					rcv = getMessage(rcv);
				} catch (IOException e) {
					if(!isConnected()) {
						Disconnect.removeSessionData(); 
						Log.v(TAG, "Disconnect code hit");
					}
					Log.e(TAG, "IOException on receive.");
				}			
			} else {
				Log.e(TAG, "Attempt to receive message from non-open socket.");
			}
			return rcv;
		}
		
		private Received getMessage(Received rcv) throws IOException {
			InputStream in = mSocket.getInputStream();
			byte buf[] = null;
			byte lenBuf[] = new byte[4];
			
			// See if any bytes are available from the Middleman
			int bytes_avail = in.available();
			int read = 0;
			
			if (bytes_avail > 0) {
				// If so, find how long the args are
				in.read(lenBuf, 0, 4);
				
				for (byte b : lenBuf) {
					String s = String.format("0x%x", b);
							
					Log.v(TAG, s);
				}
				
				ByteBuffer bb = ByteBuffer.wrap(lenBuf);
				int len = bb.getInt();
				
				if (len < 0) {
					Log.e(TAG, "Received negative length.");
					return null;
				}
				
				Log.v(TAG, "Length is: " + len);
				
				buf = new byte[len + 4 + 1]; // length and command
				
				System.arraycopy(lenBuf, 0, buf, 0, 4);
				read = in.read(buf, 4, len + 1);
				while (read < len) {
					read += in.read(buf, 4 + read, (len + 1) - read);
				}
				
			} else {
				//Log.v(TAG, "Nothing to receive.");
				return null;
			}
			
			Log.v(TAG, "Received " + buf.length + " bytes");

			if (buf.length > 4) {
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
