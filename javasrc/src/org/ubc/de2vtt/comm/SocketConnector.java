package org.ubc.de2vtt.comm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

// Async task used to connect to sockets
public class SocketConnector extends AsyncTask<String, Integer, Socket> {
	private static final String TAG = SocketConnector.class.getSimpleName();
	
	// The main parcel of work for this thread.  Opens a socket
	// to connect to the specified IP.
	@Override
	protected Socket doInBackground(String... params) {
		Socket socket = null;
		String ip = params[0];
		Integer port = Integer.decode(params[1]);
		
		if (ip == null || port == null) {
			Log.e(TAG, "Invalid parameters.");
			return null;
		}

		try {
			socket = new Socket();
			//socket.setReceiveBufferSize(1024);
			///socket.setSendBufferSize(1024);
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
		Messenger msg = Messenger.GetSharedInstance();
		msg.setSocket(s);
	}
}