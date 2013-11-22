package org.ubc.de2vtt.comm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.ubc.de2vtt.SharedPreferencesManager;
import org.ubc.de2vtt.fragments.ConnectionFragment;

import android.os.AsyncTask;
import android.util.Log;

// Async task used to connect to sockets
public class SocketConnector extends AsyncTask<String, Integer, Socket> {
	private static final String TAG = SocketConnector.class.getSimpleName();
	
	String ip;
	Integer port;
	
	// The main parcel of work for this thread.  Opens a socket
	// to connect to the specified IP.
	@Override
	protected Socket doInBackground(String... params) {
		Log.v(TAG, "Attempting to open socket.");
		Socket socket = null;
		ip = params[0];
		port = Integer.decode(params[1]);
		
		if (ip == null || port == null) {
			Log.e(TAG, "Invalid parameters.");
			return null;
		}

		try {
			socket = new Socket();
			socket.setSendBufferSize(65536);
			socket.setReceiveBufferSize(65536);
			socket.bind(null);
			socket.connect(new InetSocketAddress(ip, port), 1500);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}

	// After executing the doInBackground method, this is 
	// automatically called, in the UI (main) thread to store
	// the socket in this app's persistent storage
	protected void onPostExecute(Socket s) {
		Log.v(TAG, "onPostExecute");
		Messenger msg = Messenger.GetSharedInstance();
		msg.setSocket(s);
		if (s != null) {
			saveConnectionInfo(ip, port);
		}
		Mailbox.getSharedInstance(null).execute();
	}
	
	private void saveConnectionInfo(String ip, Integer port) {
		SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
		man.putString(ConnectionFragment.SHARED_PREFS_IP, ip);
		man.putString(ConnectionFragment.SHARED_PREFS_PORT, port.toString());
	}
}