package org.ubc.de2vtt.comm;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.TimerTask;

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

	public void openSocket(String ip, Integer port) {
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
	
	public void sendStringMessage(String msg) {		
		if (mSocket == null || mSocket.isClosed() || 
				!mSocket.isConnected()) return;
		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
		byte buf[] = new byte[msg.length() + 4];
		byte lenBuf[] = ByteBuffer.allocate(4).putInt(msg.length()).array();
		System.arraycopy(lenBuf, 0, buf, 0, lenBuf.length);
		
		System.arraycopy(msg.getBytes(), 0, buf, 4, msg.length());

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
	
	public void sendMessage(Message msg) {		
		if (mSocket == null || mSocket.isClosed() || 
				!mSocket.isConnected()) return;
		// Create an array of bytes.  First byte will be the
		// message length, and the next ones will be the message
		byte args[] = msg.GetByteArray();
		byte buf[] = new byte[args.length + 1];
		
		// Now send through the output stream of the socket
		OutputStream out;
		try {
			out = mSocket.getOutputStream();
			
			// Hopefully this buffer will allow us to send longer messages
			BufferedOutputStream bufOut = new BufferedOutputStream(out, 128);
			try {
				bufOut.write(buf, 0, buf.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String recieveStringMessage() {
		String msg = null;
		if (mSocket != null && mSocket.isConnected() 
				&& !mSocket.isClosed()) {

			try {
				InputStream in = mSocket.getInputStream();

				// See if any bytes are available from the Middleman
				int bytes_avail = in.available();
				if (bytes_avail > 0) {
					
					// If so, read them in and create a sring
					byte buf[] = new byte[bytes_avail];
					in.read(buf);

					msg = new String(buf, 0, bytes_avail, "US-ASCII");
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return msg;
	}
	
	// 256 bytes in middleman buffer
	public Message recieveMessage() {
		Message msg = null;
		if (mSocket != null && mSocket.isConnected() 
				&& !mSocket.isClosed()) {

			try {
				InputStream in = mSocket.getInputStream();

				// See if any bytes are available from the Middleman
				int bytes_avail = in.available();
				if (bytes_avail > 0) {
					
					// If so, read them in and create a sring
					byte buf[] = new byte[bytes_avail];
					in.read(buf);

					msg = new Message(buf);
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return msg;
	}

	public void closeSocket() {
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
